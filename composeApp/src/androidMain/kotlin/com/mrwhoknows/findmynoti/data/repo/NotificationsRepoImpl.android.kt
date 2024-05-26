package com.mrwhoknows.findmynoti.data.repo

import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.data.db.DriverFactory
import com.mrwhoknows.findmynoti.data.db.createDatabase
import com.mrwhoknows.findmynoti.ui.model.Notification
import com.mrwhoknows.findmynoti.ui.model.toNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of [NotificationDataSource] which talks to local Notifications sqlite DB using [DriverFactory]
 *
 * Consumed from Android App
 */
actual class NotificationDataSourceImpl(
    driverFactory: DriverFactory
) : NotificationDataSource {
    private val database by lazy {
        createDatabase(driverFactory = driverFactory)
    }

    override suspend fun getAllNotifications(): List<Notification> = withContext(Dispatchers.IO) {
        database.notificationEntityQueries.selectAll().executeAsList()
            .map(NotificationEntity::toNotification)
    }

    override suspend fun getNotificationByOffsetAndLimit(
        limit: Int,
        offset: Int,
    ): List<Notification> = withContext(Dispatchers.IO) {
        database.notificationEntityQueries.selectAllByOffsetAndLimit(
            limit.toLong(), offset.toLong()
        ).executeAsList().map(NotificationEntity::toNotification)
    }

    override suspend fun insertNotification(entity: NotificationEntity) =
        withContext(Dispatchers.IO) {
            with(entity) {
                database.notificationEntityQueries.insert(
                    id, title, content, packageName, appName, timestamp, imageUrl
                )
            }
        }

    override suspend fun getNotificationByPackageName(
        packageName: String,
        limit: Int,
        offset: Int,
    ): List<Notification> = withContext(Dispatchers.IO) {
        database.notificationEntityQueries.selectByPackageName(
            packageName,
            limit.toLong(),
            offset.toLong()
        )
            .executeAsList()
            .map(NotificationEntity::toNotification)
    }

    override suspend fun searchNotifications(
        keyword: String,
        limit: Int,
        offset: Int,
    ): List<Notification> = withContext(Dispatchers.IO) {
        val query = "%$keyword%"
        database.notificationEntityQueries.searchByTitleOrContent(query, query).executeAsList()
            .map(NotificationEntity::toNotification)
    }

}