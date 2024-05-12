package com.mrwhoknows.findmynoti

import com.mrwhoknows.findmynoti.data.HostDevice
import com.mrwhoknows.findmynoti.data.model.NotificationDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URI
import java.net.URL

class NotificationViewModel(private val hostDevice: HostDevice) {

    private val ktorClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    init {
        getNotifications()
    }

    private fun getNotifications() {
        val host = with(URI.create(hostDevice.address)) {
            "$scheme://$host:$port"
        }
        println("main host: $host")
        CoroutineScope(Dispatchers.IO).launch {
            val notifications: List<NotificationDTO> = runCatching {
                val list = ktorClient.get("$host/notifications").body<List<NotificationDTO>>()
                println("success: $list")
                list
            }.getOrElse {
                println("get error: $it")
                emptyList()
            }
            _notifications.update {
                notifications
            }
        }
    }

    private val _notifications = MutableStateFlow(listOf<NotificationDTO>())
    val notifications = _notifications.asStateFlow()

    fun search(query: String) {
        // TODO
    }

}
