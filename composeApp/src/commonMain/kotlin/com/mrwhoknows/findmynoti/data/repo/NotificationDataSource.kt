package com.mrwhoknows.findmynoti.data.repo

import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.ui.model.Notification

interface NotificationDataSource {
    suspend fun getAllNotifications(): List<Notification>
    suspend fun getNotificationByOffsetAndLimit(limit: Int, offset: Int): List<Notification>
    suspend fun insertNotification(entity: NotificationEntity)
    suspend fun searchNotifications(keyword: String, limit: Int, offset: Int): List<Notification>
    suspend fun getNotificationByPackageName(
        packageName: String, limit: Int, offset: Int,
    ): List<Notification>
}

