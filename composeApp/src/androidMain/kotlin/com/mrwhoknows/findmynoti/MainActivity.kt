package com.mrwhoknows.findmynoti

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import com.mrwhoknows.findmynoti.server.NotificationServer
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val server: NotificationServer by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo: add ui to this
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            openNotificationAccessSettings()
        }

        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var serverStarted by remember { mutableStateOf(false) }
                    val text by remember(serverStarted) {
                        mutableStateOf(
                            if (serverStarted) "Stop Server" else "Start Server"
                        )
                    }
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                serverStarted = !serverStarted
                                if (serverStarted) {
                                    server.startServer()
                                } else {
                                    server.stopServer()
                                }
                            },
                        ) {
                            Text(text)
                        }
                    }
                }
            }
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

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
