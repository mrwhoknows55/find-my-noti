package com.mrwhoknows.findmynoti

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Find My Notification",
    ) {
        App()
    }
}
