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
            /*
                        AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: HandshakeViewModel by viewModels()
                    val context = LocalContext.current
                    val scanQrCodeLauncher =
                        rememberLauncherForActivityResult(ScanQRCode()) { result ->
                            Napier.i("scanQrCodeLauncher: $result")
                            when (result) {
                                is QRResult.QRSuccess -> {
                                    if (result.content is QRContent.Url) {
                                        val desktopAddress = result.content.rawValue.orEmpty()
                                        Napier.i("desktopAddress: $desktopAddress}")
                                        viewModel.connectWithDesktop(
                                            desktopAddress,
                                            server.serverIpAddress
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
            return@setContent
             */
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

