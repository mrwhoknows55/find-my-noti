package com.mrwhoknows.findmynoti.server

import com.mrwhoknows.findmynoti.data.settings.AppSettings
import com.mrwhoknows.findmynoti.server.model.HandshakeServiceState
import com.mrwhoknows.findmynoti.server.model.HostDevice
import com.mrwhoknows.findmynoti.util.Constants
import com.mrwhoknows.findmynoti.util.Platform
import com.mrwhoknows.findmynoti.util.currentPrivateIPAddress
import com.mrwhoknows.findmynoti.util.getUnusedRandomPortNumber
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HandshakeService(
    private val platform: Platform
) {
    private lateinit var server: ApplicationEngine

    private val _result = MutableStateFlow(HandshakeServiceState())
    val result = _result.asStateFlow()

    init {
        Napier.base(DebugAntilog())
        CoroutineScope(Dispatchers.IO).launch {
            val notificationServerUrl = AppSettings.notificationServerUrl
            if (notificationServerUrl.isBlank()) {
                return@launch
            }
            tryConnectingToServer(
                notificationServerUrl,
                onSuccess = {
                    // No-Op
                },
                onFailure = {
                    // No-Op
                },
            )
        }
    }

    private fun Application.handshakeRoutes() = routing {
        get("/handshake") {
            val notificationServerUrl =
                call.request.queryParameters[Constants.NotificationServerUrl.name].orEmpty()
            Napier.i("notificationServerUrl: $notificationServerUrl")

            tryConnectingToServer(
                notificationServerUrl = notificationServerUrl,
                onSuccess = {
                    call.respond(status = HttpStatusCode.OK, message = platform.deviceName)
                },
                onFailure = {
                    call.respond(status = HttpStatusCode.ServiceUnavailable, message = "")
                },
            )
        }
    }

    private suspend fun tryConnectingToServer(
        notificationServerUrl: String,
        onSuccess: suspend () -> Unit = {},
        onFailure: suspend () -> Unit = {}
    ) {
        HttpClient().use { client ->
            val response = client.get("$notificationServerUrl/ping")
            Napier.w { "tryConnectingToServer: $response" }
            if (response.status.isSuccess()) {
                Napier.w { "tryConnectingToServer: success" }
                _result.update {
                    it.copy(
                        hostDevice = HostDevice(
                            notificationServerUrl,
                            response.bodyAsText()
                        )
                    )
                }
                AppSettings.setNotificationServerUrl(notificationServerUrl)
                onSuccess()
            } else {
                Napier.w { "tryConnectingToServer: failure" }
                _result.update {
                    it.copy(error = "connection failed")
                }
                onFailure()
            }
        }
    }

    fun startServer() {
        val portNumber = getUnusedRandomPortNumber()
        Napier.i("port number: $portNumber")
        runCatching {
            server = embeddedServer(
                CIO,
                port = portNumber,
                module = {
                    handshakeRoutes()
                },
            )
            server.start()

            val address = URLBuilder().apply {
                protocol = URLProtocol.HTTP
                host = currentPrivateIPAddress
                port = portNumber
                Napier.i("handshake server started on: $this")
                path("handshake")
            }.buildString()

            Napier.i("handshake server url: $address")

            _result.update {
                it.copy(qrCodeText = address)
            }
        }.onFailure {
            // TODO handle binder failure error
            Napier.wtf("startServer error: $it")

            _result.update { state ->
                state.copy(error = it.localizedMessage)
            }
        }
    }


    fun stopServer() = runCatching {
        server.stop()
    }.getOrElse {
        // TODO handle error
        Napier.i("stopServer: $it")
    }
}
