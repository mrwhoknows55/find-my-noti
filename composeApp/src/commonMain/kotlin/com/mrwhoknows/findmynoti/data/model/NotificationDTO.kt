package com.mrwhoknows.findmynoti.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NotificationDTO(
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
    val timestamp: Long,
    @SerialName("imageUrl")
    val imageUrl: String
)