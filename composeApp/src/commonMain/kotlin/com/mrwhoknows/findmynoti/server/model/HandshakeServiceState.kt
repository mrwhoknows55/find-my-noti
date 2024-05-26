package com.mrwhoknows.findmynoti.server.model

import androidx.compose.runtime.Stable

@Stable
data class HandshakeServiceState(
    val qrCodeText: String = "",
    val hostDevice: HostDevice? = null,
    val error: String = "",
)