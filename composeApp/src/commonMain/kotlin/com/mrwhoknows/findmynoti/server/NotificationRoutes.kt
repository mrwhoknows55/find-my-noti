package com.mrwhoknows.findmynoti.server

import com.mrwhoknows.findmynoti.data.repo.NotificationDataSource
import com.mrwhoknows.findmynoti.ui.model.Notification
import com.mrwhoknows.findmynoti.util.Constants
import com.mrwhoknows.findmynoti.util.Platform
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.notificationRoutes(
    repository: NotificationDataSource, platform: Platform
) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/ping") {
            call.respond(
                message = platform.deviceName, status = HttpStatusCode.OK
            )
        }

        get("/notifications") {
            val (offset, limit) = call.getOffsetAndLimit()
            val notifications: List<Notification> = runCatching {
                repository.getNotificationByOffsetAndLimit(limit = limit, offset = offset)
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
            val (offset, limit) = call.getOffsetAndLimit()
            val notifications: List<Notification> = runCatching {
                repository.searchNotifications(keyword, offset = offset, limit = limit)
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

/**
 * Parses offset and limit from [ApplicationCall] query parameters
 * And returns [Pair] first = offset: Int & second = limit: Int
 */
private fun ApplicationCall.getOffsetAndLimit(): Pair<Int, Int> {
    // TODO send error to client if params are not correct
    val offset = request.queryParameters[Constants.Offset.name].orEmpty().toIntOrNull() ?: 0
    val limit = request.queryParameters[Constants.Limit.name].orEmpty().toIntOrNull() ?: 20
    return offset to limit
}
