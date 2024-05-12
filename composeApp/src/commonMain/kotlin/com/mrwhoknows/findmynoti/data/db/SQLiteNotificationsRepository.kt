package com.mrwhoknows.findmynoti.data.db

import com.mrwhoknows.findmynoti.NotificationEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SQLiteNotificationsRepository(driverFactory: DriverFactory) : NotificationsRepository {

    private val database by lazy {
        createDatabase(driverFactory = driverFactory)
    }

    override suspend fun getAllNotifications(): List<NotificationEntity> = withContext(IO) {
        database.notificationEntityQueries.selectAll().executeAsList()
    }

    override suspend fun insertNotification(entity: NotificationEntity) = withContext(IO) {
        with(entity) {
            database.notificationEntityQueries.insert(
               title, content, packageName, appName, timestamp, imageUrl
            )
        }
    }

    override suspend fun getNotificationByPackageName(packageName: String): List<NotificationEntity> =
        withContext(IO) {
            database.notificationEntityQueries.selectByPackageName(packageName).executeAsList()
        }
}