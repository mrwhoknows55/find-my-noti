package com.mrwhoknows.findmynoti.ui.handshake

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrwhoknows.findmynoti.server.model.HandshakeServiceState
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun QRCodeConnectionScreen(
    handshakeServiceState: HandshakeServiceState
) = Surface(
    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
) {
    AnimatedContent(handshakeServiceState) { state ->
        when {
            state.qrCodeText.isNotBlank() -> {
                Column(
                    modifier = Modifier.safeContentPadding().fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberQrCodePainter(
                            handshakeServiceState.qrCodeText,
                            colors = QrColors(
                                dark = QrBrush.solid(MaterialTheme.colorScheme.primary),
                            )
                        ),
                        contentDescription = "QR code",
                    )

                    Spacer(Modifier.size(20.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(0.8f).padding(10.dp),
                        text = "Scan this QR code from FindMyNotification Android app to connect with Desktop",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        minLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            }

            state.error.isNotBlank() -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Error: ${state.error}")
                }
            }
        }
    }
}
