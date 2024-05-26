package com.mrwhoknows.findmynoti.di

import com.mrwhoknows.findmynoti.data.db.DriverFactory
import com.mrwhoknows.findmynoti.data.repo.NotificationDataSource
import com.mrwhoknows.findmynoti.data.repo.NotificationDataSourceImpl
import com.mrwhoknows.findmynoti.data.repo.NotificationRepository
import com.mrwhoknows.findmynoti.server.NotificationServer
import com.mrwhoknows.findmynoti.ui.NotificationListScreenModel
import com.mrwhoknows.findmynoti.ui.SettingsScreenModel
import com.mrwhoknows.findmynoti.util.getPlatform
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::DriverFactory)
    single<NotificationDataSource> { NotificationDataSourceImpl(get()) }
    single { NotificationRepository(get()) }
    singleOf(::getPlatform)
    singleOf(::NotificationServer)
}

val notificationListModule = module {
    factory { NotificationListScreenModel(get()) }
}

val settingsModule = module {
    factory { SettingsScreenModel(get()) }
}