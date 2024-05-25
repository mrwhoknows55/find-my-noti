package com.mrwhoknows.findmynoti.server

import com.mrwhoknows.findmynoti.data.repo.NotificationsRepository
import com.mrwhoknows.findmynoti.util.Platform
import com.mrwhoknows.findmynoti.util.currentPrivateIPAddress
import com.mrwhoknows.findmynoti.util.getUnusedRandomPortNumber
import io.github.aakira.napier.Napier
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

class NotificationServer(
    private val repository: NotificationsRepository,
    private val platform: Platform
) {

    private lateinit var server: NettyApplicationEngine
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
            server =
                embeddedServer(
                    Netty,
                    port = portNumber,
                    module = { notificationRoutes(repository, platform = platform) })
            Napier.i("startServer: ${server.application}")
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