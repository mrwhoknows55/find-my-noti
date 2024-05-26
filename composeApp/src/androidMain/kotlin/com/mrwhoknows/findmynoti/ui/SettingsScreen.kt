package com.mrwhoknows.findmynoti.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import com.mrwhoknows.findmynoti.util.showShortToast
import io.github.aakira.napier.Napier
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import io.github.g00fy2.quickie.content.QRContent

class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        AppTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                val screenModel: SettingsScreenModel = koinScreenModel<SettingsScreenModel>()
                val context = LocalContext.current
                val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
                    Napier.i("scanQrCodeLauncher: $result")
                    when (result) {
                        is QRResult.QRSuccess -> {
                            if (result.content is QRContent.Url) {
                                val desktopAddress = result.content.rawValue.orEmpty()
                                Napier.i("desktopAddress: $desktopAddress}")
                                screenModel.connectWithDesktop(desktopAddress)
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
                // TODO get from local settings
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
                                screenModel.startServer()
                            } else {
                                screenModel.stopServer()
                            }
                        },
                    ) {
                        Text(text)
                    }

                    val deviceName by screenModel.connectedDeviceName.collectAsState()
                    if (serverStarted) {
                        Button(
                            enabled = deviceName.isEmpty(),
                            onClick = {
                                scanQrCodeLauncher.launch(null)
                            }
                        ) {
                            Text(deviceName.ifBlank { "Connect to desktop" })
                        }
                    }
                }
            }
        }
    }
}