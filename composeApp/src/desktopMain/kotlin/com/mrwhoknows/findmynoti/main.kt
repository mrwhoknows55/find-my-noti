package com.mrwhoknows.findmynoti

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mrwhoknows.findmynoti.server.HostDevice
import com.mrwhoknows.findmynoti.server.HandshakeService
import com.mrwhoknows.findmynoti.ui.handshake.QRCodeConnectionScreen
import com.mrwhoknows.findmynoti.ui.noti.NotificationViewModel
import com.mrwhoknows.findmynoti.ui.noti.NotificationsScreen
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import org.jetbrains.skiko.hostId

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Find My Notification",
        alwaysOnTop = true,
    ) {

        val handshakeService = HandshakeService()
        LaunchedEffect(Unit) {
            handshakeService.startServer()
        }
        val handshakeServiceState by handshakeService.result.collectAsState()
        AppTheme {
            AnimatedContent(handshakeServiceState) { handshakeServiceState ->
                if (handshakeServiceState.hostDevice == null) {
                    QRCodeConnectionScreen(handshakeServiceState)
                } else {
                    val notificationViewModel = NotificationViewModel(handshakeServiceState.hostDevice)
                    NotificationsScreen(notificationViewModel)
                }
            }

        }
    }
}

