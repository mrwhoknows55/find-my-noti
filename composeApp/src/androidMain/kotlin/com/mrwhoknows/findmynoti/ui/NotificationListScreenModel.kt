package com.mrwhoknows.findmynoti.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.data.db.SQLiteNotificationsRepository
import com.mrwhoknows.findmynoti.ui.model.Notification
import com.mrwhoknows.findmynoti.ui.model.toNotification
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationListScreenModel(
    private val notificationsRepository: SQLiteNotificationsRepository
) : ScreenModel {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private fun getAllNotifications() {
        screenModelScope.launch(Dispatchers.IO) {
            val notifications = runCatching {
                notificationsRepository.getNotificationByOffsetAndLimit(limit = 50, offset = 0)
                    .map(NotificationEntity::toNotification)
            }.getOrElse {
                if (it is CancellationException) {
                    throw it
                }
                Napier.e(it.localizedMessage, it)
                emptyList()
            }
            _notifications.update { notifications }
        }
    }

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            getAllNotifications()
            return
        }
        screenModelScope.launch(Dispatchers.IO) {
            val notifications = runCatching {
                notificationsRepository.searchNotifications(keyword)
                    .map(NotificationEntity::toNotification)
            }.getOrElse {
                if (it is CancellationException) {
                    throw it
                }
                Napier.e(it.localizedMessage, it)
                emptyList()
            }
            _notifications.update { notifications }
        }
    }

    init {
        getAllNotifications()
    }
}