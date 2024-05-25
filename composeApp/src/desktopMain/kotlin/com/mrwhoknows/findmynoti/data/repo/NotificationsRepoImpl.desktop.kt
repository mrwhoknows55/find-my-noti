package com.mrwhoknows.findmynoti.data.repo

import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.server.HostDevice
import com.mrwhoknows.findmynoti.ui.model.Notification
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException

/**
 * Implementation of [NotificationsRepository] which talks to ktor server hosted on Android app [com.mrwhoknows.findmynoti.server.NotificationServer]
 *
 * Consumed from Desktop App
 */
actual class NotificationsRepositoryImpl(
    hostDevice: HostDevice
) : NotificationsRepository {
    private val host = hostDevice.hostUrl

    private val ktorClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    override suspend fun getAllNotifications(): List<Notification> = runCatching {
        val list = ktorClient.get("$host/notifications").body<List<Notification>>()
        Napier.i("success: $list")
        list
    }.getOrElse {
        if (it is CancellationException) throw it
        Napier.i("get error: $it")
        emptyList()
    }

    override suspend fun searchNotifications(keyword: String): List<Notification> = runCatching {
        val list = ktorClient.get("$host/search/$keyword").body<List<Notification>>()
        Napier.i("searchNotifications success: $list")
        list
    }.getOrElse {
        Napier.i("searchNotifications error: $it")
        emptyList()
    }


    override suspend fun getNotificationByOffsetAndLimit(
        limit: Long, offset: Long
    ): List<Notification> = runCatching {
        val list = ktorClient.get("$host/notifications").body<List<Notification>>()
        Napier.i("success: $list")
        list
    }.getOrElse {
        if (it is CancellationException) throw it
        Napier.i("get error: $it")
        emptyList()
    }

    override suspend fun insertNotification(entity: NotificationEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getNotificationByPackageName(packageName: String): List<Notification> {
        TODO("Not yet implemented")
    }
}