package com.mrwhoknows.findmynoti.ui.model

import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.server.model.NotificationDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

data class Notification(
    val id: Long,
    val title: String,
    val content: String,
    val packageName: String,
    val appName: String,
    val dateTime: String,
    val imageUrl: String?,
)


fun Long.formatDateTime() =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(currentSystemDefault()).let {
        val currentYear = Clock.System.now().toLocalDateTime(currentSystemDefault()).year
        it.format(LocalDateTime.Format {
            dayOfMonth(Padding.SPACE)
            char(' ')
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            if (it.year != currentYear) {
                char(' ')
                year()
            }
            char(',')
            char(' ')
            amPmHour()
            chars(":")
            minute()
            char(' ')
            amPmMarker("AM", "PM")
        })
    }

fun NotificationEntity.toNotification() = Notification(
    id = id,
    title = title.ifBlank { "No title" },
    content = content.ifBlank { "Empty Content" },
    packageName = packageName.ifBlank { "Unknown" },
    appName = appName.orEmpty().ifBlank { packageName.ifBlank { "Unknown" } },
    dateTime = timestamp.formatDateTime(),
    imageUrl = imageUrl
)

fun NotificationDTO.toNotification() = Notification(
    id = id,
    title = title.ifBlank { "No title" },
    content = content.ifBlank { "Empty Content" },
    packageName = packageName.ifBlank { "Unknown" },
    appName = appName.ifBlank { packageName.ifBlank { "Unknown" } },
    dateTime = timestamp.formatDateTime(),
    imageUrl = imageUrl
)