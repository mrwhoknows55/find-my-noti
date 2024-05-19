package com.mrwhoknows.findmynoti.data.settings

import android.content.Context
import com.mrwhoknows.findmynoti.MainApplication
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

private val delegate by lazy {
    MainApplication.applicationContext.getSharedPreferences("Settings", Context.MODE_PRIVATE)
}
actual val settings: Settings = SharedPreferencesSettings(delegate)