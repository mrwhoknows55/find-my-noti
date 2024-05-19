package com.mrwhoknows.findmynoti.data.settings

import com.mrwhoknows.findmynoti.util.Constants.NotificationServerUrl
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

expect val settings: Settings

object AppSettings {

    val notificationServerUrl: String
        get() = settings[NotificationServerUrl.name] ?: ""

    fun setNotificationServerUrl(url: String) {
        settings[NotificationServerUrl.name] = url
    }

}
