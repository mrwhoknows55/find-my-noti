package com.mrwhoknows.findmynoti.data.repo

import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.server.model.HostDevice
import com.mrwhoknows.findmynoti.ui.model.Notification
import com.mrwhoknows.findmynoti.util.Constants.Limit
import com.mrwhoknows.findmynoti.util.Constants.Offset
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Implementation of [NotificationDataSource] which talks to ktor server hosted on Android app [com.mrwhoknows.findmynoti.server.NotificationServer]
 *
 * Consumed from Desktop App
 */
actual class NotificationDataSourceImpl(
    hostDevice: HostDevice
) : NotificationDataSource {
    private val host = hostDevice.hostUrl

    private val json by lazy {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    private val ktorClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(json)
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

    override suspend fun searchNotifications(
        keyword: String,
        limit: Int,
        offset: Int,
    ): List<Notification> = runCatching {
        val url = URLBuilder("$host/search/$keyword").apply {
            parameters.append(Limit.name, limit.toString())
            parameters.append(Offset.name, offset.toString())
        }.build()
        val list = ktorClient.get(url).body<List<Notification>>()
        Napier.i("searchNotifications success: $list")
        list
    }.getOrElse {
        Napier.i("searchNotifications error: $it")
        emptyList()
    }


    override suspend fun getNotificationByOffsetAndLimit(
        limit: Int, offset: Int
    ): List<Notification> = runCatching {
        val url = URLBuilder("$host/notifications").apply {
            parameters.append(Limit.name, limit.toString())
            parameters.append(Offset.name, offset.toString())
        }.build()
        val list = ktorClient.get(url).body<List<Notification>>()
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

    override suspend fun getNotificationByPackageName(
        packageName: String,
        limit: Int,
        offset: Int,
    ): List<Notification> {
        TODO("Not yet implemented")
    }


    // some weird bug with ktor kotlinx-serialization with proguard for now doing this fixes it
    // https://youtrack.jetbrains.com/issue/KTOR-5898
    init {
        val notificationListSerializer: KSerializer<List<Notification>> =
            ListSerializer(Notification.serializer())
        Napier.i { "notificationListSerializer: $notificationListSerializer" }
    }

}