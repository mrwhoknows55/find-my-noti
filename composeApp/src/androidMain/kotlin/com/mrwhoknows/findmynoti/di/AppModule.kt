package com.mrwhoknows.findmynoti.di

import com.mrwhoknows.findmynoti.data.db.DriverFactory
import com.mrwhoknows.findmynoti.data.db.SQLiteNotificationsRepository
import com.mrwhoknows.findmynoti.server.NotificationServer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::DriverFactory)
    singleOf(::SQLiteNotificationsRepository)
    singleOf(::NotificationServer)
}