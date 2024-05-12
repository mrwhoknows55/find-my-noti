package com.mrwhoknows.findmynoti.data.db

import app.cash.sqldelight.db.SqlDriver
import com.mrwhoknows.findmynoti.NotificationsDB

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): NotificationsDB {
    val driver = driverFactory.createDriver()
    val database = NotificationsDB(driver)
    return database
}