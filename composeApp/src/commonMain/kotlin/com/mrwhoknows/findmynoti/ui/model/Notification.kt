package com.mrwhoknows.findmynoti.ui.model

import com.mrwhoknows.findmynoti.NotificationEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Notification(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("packageName")
    val packageName: String,
    @SerialName("appName")
    val appName: String,
    @SerialName("timestamp")
    val dateTime: String,
    @SerialName("imageUrl")
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