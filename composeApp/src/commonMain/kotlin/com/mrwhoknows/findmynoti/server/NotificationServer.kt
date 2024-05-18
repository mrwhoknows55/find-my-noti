package com.mrwhoknows.findmynoti.server

import com.mrwhoknows.findmynoti.Platform
import com.mrwhoknows.findmynoti.data.db.SQLiteNotificationsRepository
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

class NotificationServer(
    private val repository: SQLiteNotificationsRepository,
    private val platform: Platform
) {

    private lateinit var server: NettyApplicationEngine

    fun startServer() {
        runCatching {
            server =
                embeddedServer(
                    Netty,
                    port = 1337,
                    module = { notificationRoutes(repository, platform = platform) })
            println("startServer: ${server.application}")
            server.start()
        }.onFailure {
            // TODO handle binder failure error
            println("startServer: $it")
        }
    }

    fun stopServer() = kotlin.runCatching {
        server.stop()
    }.getOrElse {
        // TODO handle error
        println("stopServer: $it")
    }
}