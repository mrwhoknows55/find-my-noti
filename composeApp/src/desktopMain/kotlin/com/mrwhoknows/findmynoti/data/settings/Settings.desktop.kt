package com.mrwhoknows.findmynoti.data.settings

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

private val preferences = Preferences.userRoot()
actual val settings: Settings = PreferencesSettings(preferences)
