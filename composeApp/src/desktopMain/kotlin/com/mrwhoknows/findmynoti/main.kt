package com.mrwhoknows.findmynoti

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.mrwhoknows.findmynoti.data.repo.NotificationDataSourceImpl
import com.mrwhoknows.findmynoti.data.repo.NotificationRepository
import com.mrwhoknows.findmynoti.server.HandshakeService
import com.mrwhoknows.findmynoti.ui.NotificationListScreenModel
import com.mrwhoknows.findmynoti.ui.handshake.QRCodeConnectionScreen
import com.mrwhoknows.findmynoti.ui.noti.NotificationsScreen
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import com.mrwhoknows.findmynoti.util.isDebug
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import java.awt.Dimension

fun main() = application {
    if (isDebug) {
        Napier.base(DebugAntilog())
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Find My Notification",
        alwaysOnTop = true,
    ) {
        window.minimumSize = Dimension(800, 600)
        Navigator(MainScreen())
    }
}

class MainScreen : Screen {
    @Composable
    override fun Content() {
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
                        handshakeService.stopServer()
                        val notificationListScreenModel = rememberScreenModel {
                            NotificationListScreenModel(
                                NotificationRepository(
                                    NotificationDataSourceImpl(handshakeServiceState.hostDevice)
                                )
                            )
                        }
                        NotificationsScreen(notificationListScreenModel)
                    }
                }
            }
        }
    }
}

