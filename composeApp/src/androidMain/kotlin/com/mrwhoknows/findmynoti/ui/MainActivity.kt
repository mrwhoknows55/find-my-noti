package com.mrwhoknows.findmynoti.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.NotificationManagerCompat
import cafe.adriel.voyager.navigator.Navigator
import com.mrwhoknows.findmynoti.server.NotificationServer
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val server: NotificationServer by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // todo: add ui to this
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            openNotificationAccessSettings()
        }

        setContent {
            Navigator(NotificationListScreen())
        }
    }

    private fun openNotificationAccessSettings() {
        activityForResultLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private val activityForResultLauncher = registerForActivityResult(StartActivityForResult()) {
        // todo: handle ui
    }

    override fun onDestroy() {
        super.onDestroy()
        server.startServer()
        server.stopServer()
    }
}

