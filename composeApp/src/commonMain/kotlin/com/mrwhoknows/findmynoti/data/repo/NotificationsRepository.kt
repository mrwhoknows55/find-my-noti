package com.mrwhoknows.findmynoti.data.repo

import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.ui.model.Notification

interface NotificationsRepository {
    suspend fun getAllNotifications(): List<Notification>
    suspend fun getNotificationByOffsetAndLimit(limit: Long, offset: Long): List<Notification>
    suspend fun insertNotification(entity: NotificationEntity)
    suspend fun getNotificationByPackageName(packageName: String): List<Notification>
    suspend fun searchNotifications(keyword: String): List<Notification>
}