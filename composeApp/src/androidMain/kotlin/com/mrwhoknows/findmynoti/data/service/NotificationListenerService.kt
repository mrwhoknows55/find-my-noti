package com.mrwhoknows.findmynoti.data.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.data.repo.NotificationDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


private const val TAG = "NotificationListenerSer"

class NotificationListenerService : NotificationListenerService() {

    private val notificationsRepository: NotificationDataSource by inject()

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
        val title = extras.getCharSequence(Notification.EXTRA_TITLE, "").toString()
        val content = extras.getCharSequence(Notification.EXTRA_TEXT, "").toString()
        Log.d(TAG, "onNotificationPosted: $title -> $content -> sbn: $sbn")
        Log.i(TAG, "onNotificationPosted: extras: $extras")
        val timestamp = sbn.postTime
        val image = extras.getString(Notification.EXTRA_PICTURE, "").orEmpty()

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
                id = sbn.id.toLong(),
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