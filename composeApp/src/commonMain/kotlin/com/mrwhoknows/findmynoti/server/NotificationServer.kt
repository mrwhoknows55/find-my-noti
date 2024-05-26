package com.mrwhoknows.findmynoti.server

import com.mrwhoknows.findmynoti.data.repo.NotificationDataSource
import com.mrwhoknows.findmynoti.util.Platform
import com.mrwhoknows.findmynoti.util.currentPrivateIPAddress
import com.mrwhoknows.findmynoti.util.getUnusedRandomPortNumber
import io.github.aakira.napier.Napier
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer

class NotificationServer(
    private val repository: NotificationDataSource, private val platform: Platform
) {

    private lateinit var server: ApplicationEngine
    private val portNumber by lazy {
        getUnusedRandomPortNumber()
    }

    val serverIpAddress: String
        get() {
            Napier.i("currentPrivateIPAddress: $currentPrivateIPAddress")
            return URLBuilder().apply {
                protocol = URLProtocol.HTTP
                host = currentPrivateIPAddress
                port = portNumber
            }.buildString()
        }

    fun startServer() {

        runCatching {
            server = embeddedServer(
                CIO,
                port = portNumber,
                module = { notificationRoutes(repository, platform = platform) },
            )
            server.start()
        }.onFailure {
            // TODO handle binder failure error
            Napier.i("startServer: $it")
        }
    }

    fun stopServer() = kotlin.runCatching {
        server.stop()
    }.getOrElse {
        // TODO handle error
        Napier.i("stopServer: $it")
    }
}