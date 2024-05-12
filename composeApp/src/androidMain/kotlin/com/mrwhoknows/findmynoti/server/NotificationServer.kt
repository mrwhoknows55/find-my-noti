package com.mrwhoknows.findmynoti.server

import android.util.Log
import com.mrwhoknows.findmynoti.data.db.SQLiteNotificationsRepository
import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

private const val TAG = "NotificationServer"

class NotificationServer(
    private val repository: SQLiteNotificationsRepository
) {

    private lateinit var server: NettyApplicationEngine

    fun startServer() {
        runCatching {
            server =
                embeddedServer(Netty, port = 1337, module = { module(repository = repository) })
            Log.d(TAG, "startServer: ${server.application.log}")
            server.start()
        }.onFailure {
            // TODO handle binder failure error
            Log.e(TAG, "startServer: $it")
        }
    }

    fun stopServer() = kotlin.runCatching {
        server.stop()
    }.getOrElse {
        // TODO handle error
        Log.e(TAG, "stopServer: $it")
        Unit
    }
}

