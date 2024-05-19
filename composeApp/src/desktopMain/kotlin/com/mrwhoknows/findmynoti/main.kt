package com.mrwhoknows.findmynoti

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mrwhoknows.findmynoti.server.HandshakeService
import com.mrwhoknows.findmynoti.ui.handshake.QRCodeConnectionScreen
import com.mrwhoknows.findmynoti.ui.noti.NotificationViewModel
import com.mrwhoknows.findmynoti.ui.noti.NotificationsScreen
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Find My Notification",
        alwaysOnTop = true,
    ) {
        window.minimumSize = Dimension(800, 600)

        val handshakeService = HandshakeService()
        LaunchedEffect(Unit) {
            handshakeService.startServer()
        }
        val handshakeServiceState by handshakeService.result.collectAsState()
        AppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorScheme.surface,
            ) {
                AnimatedContent(handshakeServiceState) { handshakeServiceState ->

                    if (handshakeServiceState.error.isNotBlank()) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Error: ${handshakeServiceState.error}",
                                color = colorScheme.onSurface
                            )
                        }
                        return@AnimatedContent
                    }

                    if (handshakeServiceState.hostDevice == null) {
                        QRCodeConnectionScreen(handshakeServiceState)
                    } else {
                        val notificationViewModel =
                            NotificationViewModel(handshakeServiceState.hostDevice)
                        NotificationsScreen(notificationViewModel)
                    }
                }
            }
        }
    }
}

