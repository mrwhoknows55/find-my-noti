package com.mrwhoknows.findmynoti

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrwhoknows.findmynoti.server.NotificationServer
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import com.mrwhoknows.findmynoti.util.showShortToast
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import io.github.g00fy2.quickie.content.QRContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class HandshakeViewModel : ViewModel() {

    fun connectWithDesktop(desktopUrl: String, notificationServerUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = URLBuilder(desktopUrl).apply {
                parameters.append("notificationServerUrl", notificationServerUrl)
            }.build()
            println("connectWithDesktop: $url")
            val result = HttpClient().use {
                it.get(url)
            }
            println(result.bodyAsText())
        }
    }
}

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
                    val viewModel: HandshakeViewModel by viewModels()
                    val context = LocalContext.current
                    val scanQrCodeLauncher =
                        rememberLauncherForActivityResult(ScanQRCode()) { result ->
                            println("scanQrCodeLauncher: $result")
                            // handle QRResult
                            when (result) {
                                is QRResult.QRSuccess -> {
                                    if (result.content is QRContent.Url) {
                                        println("desktopIP: ${result.content.rawValue.toString()}")
                                        viewModel.connectWithDesktop(
                                            result.content.rawValue.orEmpty(),
                                            "http://192.168.1.6:1337"
                                        )
                                    } else {
                                        context.showShortToast("invalid qr code")
                                    }
                                }

                                is QRResult.QRError -> {
                                    context.showShortToast("error: ${result.exception}")
                                }

                                QRResult.QRMissingPermission -> {
                                    context.showShortToast("camera permissions required to scan QR code")
                                }

                                QRResult.QRUserCanceled -> {
                                    // no-op
                                }
                            }
                        }
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

                        if (serverStarted) {
                            Button({
                                scanQrCodeLauncher.launch(null)
                            }) {
                                Text("Connect to desktop")
                            }
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

