package com.mrwhoknows.findmynoti.server

import android.os.Build
import android.util.Log
import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.data.db.SQLiteNotificationsRepository
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

// TODO can be moved to shared module
private const val TAG = "Routes"
fun Application.module(repository: SQLiteNotificationsRepository) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/ping") {
            call.respond(
                message = Build.MANUFACTURER + " " + Build.MODEL.ifBlank { Build.PRODUCT },
                status = HttpStatusCode.OK
            )
        }

        // TODO add pagination
        get("/notifications") {
            val notifications: List<NotificationDTO> = runCatching {
                repository.getAllNotifications().take(50).map {
                    NotificationDTO(
                        id = it.id,
                        title = it.title,
                        content = it.content,
                        packageName = it.packageName,
                        appName = it.appName.orEmpty().ifBlank { it.packageName },
                        timestamp = it.timestamp,
                        imageUrl = it.imageUrl.orEmpty()
                    )
                }
            }.getOrElse {
                Log.e(TAG, "/notifications: $it")
                emptyList()
            }
            Log.i(TAG, "/notifications: ${notifications.size}: $notifications")
            kotlin.runCatching {
                call.respond(notifications)
            }.getOrElse {
                Log.e(TAG, "/notifications respond: $it")
            }
        }
    }
}

