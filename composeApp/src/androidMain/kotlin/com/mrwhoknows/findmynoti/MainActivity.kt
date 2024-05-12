package com.mrwhoknows.findmynoti

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo: add ui to this
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            openNotificationAccessSettings()
        }
        setContent {
            Column {
                App()
            }
        }
    }

    private fun openNotificationAccessSettings() {
        activityForResultLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private val activityForResultLauncher = registerForActivityResult(StartActivityForResult()) {
        // todo: handle ui
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
