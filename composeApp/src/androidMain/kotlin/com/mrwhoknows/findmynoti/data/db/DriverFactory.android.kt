package com.mrwhoknows.findmynoti.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mrwhoknows.findmynoti.NotificationsDB

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NotificationsDB.Schema, context, "notifications.db")
    }
}