package com.mrwhoknows.findmynoti

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mrwhoknows.findmynoti.data.DeviceFinder
import com.mrwhoknows.findmynoti.data.HostDevice
import com.mrwhoknows.findmynoti.ui.theme.AppTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Find My Notification",
    ) {
        // TODO use koin to inject
        val viewModel = MainViewModel(DeviceFinder())

        // FIXME: get it from navigation
        var selectedHostDevice: HostDevice? by remember {
            mutableStateOf(null)
        }
        AppTheme {

            AnimatedContent(selectedHostDevice) { hostDevice ->
                if (hostDevice == null) {
                    MainScreen(viewModel) {
                        viewModel.stopFinding()
                        selectedHostDevice = it
                    }
                } else {
                    val notificationViewModel = NotificationViewModel(hostDevice)
                    NotificationsScreen(notificationViewModel)
                }
            }

        }
    }
}
