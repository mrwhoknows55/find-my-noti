package com.mrwhoknows.findmynoti.server

import androidx.compose.runtime.Stable
import com.mrwhoknows.findmynoti.util.currentPrivateIPAddress
import com.mrwhoknows.findmynoti.util.getUnusedRandomPortNumber
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
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.URI

@Stable
data class HostDevice(
    val address: String,
    val deviceName: String
) {
    val hostUrl: String
        get() = with(URI.create(address)) {
            "$scheme://$host:$port"
        }
}

@Stable
data class HandshakeServiceState(
    val qrCodeText: String = "",
    val hostDevice: HostDevice? = null,
    val error: String = "",
)

class HandshakeService {
    private lateinit var server: NettyApplicationEngine

    private val _result = MutableStateFlow(HandshakeServiceState())
    val result = _result.asStateFlow()

    private fun Application.handshakeRoutes() = routing {
        get("/handshake") {
            val notificationServerUrl =
                call.request.queryParameters["notificationServerUrl"].orEmpty()
            Napier.i("notificationServerUrl: $notificationServerUrl")

            HttpClient().use { client ->
                val response = client.get("$notificationServerUrl/ping")
                if (response.status.isSuccess()) {
                    _result.update {
                        it.copy(
                            hostDevice = HostDevice(
                                notificationServerUrl,
                                response.bodyAsText()
                            )
                        )
                    }
                    call.respond(status = HttpStatusCode.Accepted, "Connection established")
                } else {
                    _result.update {
                        it.copy(error = "connection failed")
                    }
                    call.respond(status = HttpStatusCode.NotFound, "Connection Failed")
                }
            }

        }
    }

    fun startServer() {
        val portNumber = getUnusedRandomPortNumber()
        Napier.i("port number: $portNumber")
        runCatching {
            server = embeddedServer(
                Netty,
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
            Napier.i("startServer: $it")

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
