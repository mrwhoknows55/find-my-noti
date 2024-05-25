package com.mrwhoknows.findmynoti.ui.noti

import com.mrwhoknows.findmynoti.server.HostDevice
import com.mrwhoknows.findmynoti.server.model.NotificationDTO
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class NotificationViewModel(hostDevice: HostDevice) {
    private val host = hostDevice.hostUrl

    private val ktorClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    private val _searchKeyword = MutableStateFlow("")
    private val _notifications = MutableStateFlow(listOf<NotificationDTO>())
    val notifications = _notifications.asStateFlow()

    fun search(keyword: String) {
        _searchKeyword.update { keyword }
    }

    private var searchJob: Job? = null
    private fun searchNotifications(keyword: String) {
        if (keyword.isBlank()) {
            getNotifications()
            return
        }
        searchJob?.cancel()
        fetchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.IO).launch {
            val notifications: List<NotificationDTO> = runCatching {
                val list = ktorClient.get("$host/search/$keyword").body<List<NotificationDTO>>()
                Napier.i("searchNotifications success: $list")
                list
            }.getOrElse {
                Napier.i("searchNotifications error: $it")
                emptyList()
            }
            _notifications.update {
                notifications
            }
        }
    }

    private var fetchJob: Job? = null
    private fun getNotifications() {
        fetchJob?.cancel()
        searchJob?.cancel()
        fetchJob = CoroutineScope(Dispatchers.IO).launch {
            val notifications: List<NotificationDTO> = runCatching {
                val list = ktorClient.get("$host/notifications").body<List<NotificationDTO>>()
                Napier.i("success: $list")
                list
            }.getOrElse {
                Napier.i("get error: $it")
                emptyList()
            }
            _notifications.update {
                notifications
            }
        }
    }


    init {
        getNotifications()
        CoroutineScope(Dispatchers.IO).launch {
            _searchKeyword.debounce(200).collectLatest {
                searchNotifications(it)
            }
        }
    }

}
