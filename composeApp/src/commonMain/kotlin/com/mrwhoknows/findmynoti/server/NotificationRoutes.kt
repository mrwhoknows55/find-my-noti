package com.mrwhoknows.findmynoti.server

import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.data.db.SQLiteNotificationsRepository
import com.mrwhoknows.findmynoti.server.model.NotificationDTO
import com.mrwhoknows.findmynoti.server.model.toDTO
import com.mrwhoknows.findmynoti.util.Platform
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.notificationRoutes(
    repository: SQLiteNotificationsRepository,
    platform: Platform
) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/ping") {
            call.respond(
                message = platform.deviceName,
                status = HttpStatusCode.OK
            )
        }

        // TODO add pagination
        get("/notifications") {
            val notifications: List<NotificationDTO> = runCatching {
                repository.getNotificationByOffsetAndLimit(limit = 0, offset = 50).map(NotificationEntity::toDTO)
            }.getOrElse {
                Napier.i("/notifications: $it")
                emptyList()
            }
            Napier.i("/notifications: ${notifications.size}: $notifications")
            kotlin.runCatching {
                call.respond(notifications)
            }.getOrElse {
                Napier.i("/notifications respond: $it")
            }
        }

        get("/search/{keyword}") {
            val keyword = call.parameters["keyword"].orEmpty()
            val notifications: List<NotificationDTO> = runCatching {
                repository.searchNotifications(keyword).map(NotificationEntity::toDTO)
            }.getOrElse {
                Napier.i("/notifications: $it")
                emptyList()
            }
            Napier.i("/notifications: ${notifications.size}: $notifications")
            kotlin.runCatching {
                call.respond(notifications)
            }.getOrElse {
                Napier.i("/notifications respond: $it")
            }
        }
    }
}

