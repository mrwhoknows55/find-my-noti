package com.mrwhoknows.findmynoti

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.mrwhoknows.findmynoti.data.db.SQLiteNotificationsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val TAG = "NotificationListenerSer"

class NotificationListenerService : NotificationListenerService() {

    private val notificationsRepository: SQLiteNotificationsRepository by inject()

    private fun insertNotification(notificationEntity: NotificationEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                notificationsRepository.insertNotification(notificationEntity)
            }.onFailure {
                it.printStackTrace()
                if (it is CancellationException) {
                    throw it
                }
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras
        Log.d(TAG, "onNotificationPosted: extras: $extras")
        val title = extras.getCharSequence(Notification.EXTRA_TITLE, "").toString()
        val content = extras.getCharSequence(Notification.EXTRA_TEXT, "").toString()
        val timestamp = sbn.postTime
        val image = extras.getString(Notification.EXTRA_PICTURE).orEmpty()

        val appName: String = with(applicationContext.packageManager) {
            return@with kotlin.runCatching {
                getApplicationLabel(getApplicationInfo(packageName, 0)).toString()
            }.getOrElse {
                it.printStackTrace()
                ""
            }
        }

        insertNotification(
            NotificationEntity(
                id = -1, // auto increments
                title = title,
                content = content,
                packageName = packageName,
                appName = appName,
                timestamp = timestamp,
                imageUrl = image
            )
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG, "onNotificationRemoved: $sbn")
        // No-Op
    }
}

