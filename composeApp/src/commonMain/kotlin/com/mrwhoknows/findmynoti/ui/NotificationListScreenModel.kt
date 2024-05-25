package com.mrwhoknows.findmynoti.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mrwhoknows.findmynoti.data.repo.NotificationsRepository
import com.mrwhoknows.findmynoti.ui.model.Notification
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
class NotificationListScreenModel(
    private val repository: NotificationsRepository
) : ScreenModel {
    private val _searchKeyword = MutableStateFlow("")
    private val _notifications = MutableStateFlow(listOf<Notification>())
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
            val result = repository.searchNotifications(keyword)
            _notifications.update { result }
        }
    }

    private var fetchJob: Job? = null
    private fun getNotifications() {
        fetchJob?.cancel()
        searchJob?.cancel()
        fetchJob = screenModelScope.launch(Dispatchers.IO) {
            // FIXME: pagination
            val notifications = repository.getNotificationByOffsetAndLimit(limit = 50, 0)
            _notifications.update {
                notifications
            }
        }
    }

    init {
        getNotifications()
        screenModelScope.launch(Dispatchers.IO) {
            _searchKeyword.debounce(200).collectLatest {
                searchNotifications(it)
            }
        }
    }
}