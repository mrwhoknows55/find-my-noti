package com.mrwhoknows.findmynoti.server.model

import androidx.compose.runtime.Stable
import java.net.URI

@Stable
data class HostDevice(
    val address: String,
    val deviceName: String
) {
    val hostUrl: String
        get() = with(URI.create(address)) {
            "$scheme://$host:$port"
        }
}