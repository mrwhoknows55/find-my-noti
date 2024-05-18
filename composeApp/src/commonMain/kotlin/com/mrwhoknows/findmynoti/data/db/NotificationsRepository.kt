package com.mrwhoknows.findmynoti.data.db

import com.mrwhoknows.findmynoti.NotificationEntity

interface NotificationsRepository {
    suspend fun getAllNotifications(): List<NotificationEntity>
    suspend fun getNotificationByOffsetAndLimit(limit: Long, offset: Long): List<NotificationEntity>
    suspend fun insertNotification(entity: NotificationEntity)
    suspend fun getNotificationByPackageName(packageName: String): List<NotificationEntity>
    suspend fun searchNotifications(keyword: String): List<NotificationEntity>
}