package com.mrwhoknows.findmynoti.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mrwhoknows.findmynoti.server.NotificationServer
import com.mrwhoknows.findmynoti.util.Constants.NotificationServerUrl
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val server: NotificationServer,
) : ScreenModel {
    // todo use explicit backing field
    private val _connectedDeviceName = MutableStateFlow("")
    val connectedDeviceName = _connectedDeviceName.asStateFlow()

    fun connectWithDesktop(desktopUrl: String) {
        screenModelScope.launch(Dispatchers.IO) {
            val url = URLBuilder(desktopUrl).apply {
                parameters.append(NotificationServerUrl.name, server.serverIpAddress)
            }.build()
            Napier.i("connectWithDesktop: $url")
            val result = HttpClient().use {
                it.get(url)
            }
            Napier.i("handshake result -> " + result.bodyAsText())
            _connectedDeviceName.update {
                if (result.status == HttpStatusCode.OK) {
                    "Connected to: ${result.bodyAsText()}"
                } else {
                    ""
                }
            }
        }
    }

    fun startServer() = server.startServer().also { _connectedDeviceName.update { "" } }
    fun stopServer() = server.stopServer().also { _connectedDeviceName.update { "" } }

    override fun onDispose() {
        super.onDispose()
        // FIXME: keep this till adding "keep server running in background" option
        stopServer()
    }
}