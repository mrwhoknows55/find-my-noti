package com.mrwhoknows.findmynoti.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mrwhoknows.findmynoti.NotificationsDB

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db")
        NotificationsDB.Schema.create(driver)
        return driver
    }
}