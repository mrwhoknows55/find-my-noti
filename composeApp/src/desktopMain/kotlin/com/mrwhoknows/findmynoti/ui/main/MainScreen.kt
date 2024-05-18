package com.mrwhoknows.findmynoti.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrwhoknows.findmynoti.server.HostDevice
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun MainScreen(mainViewModel: MainViewModel, onHostDeviceClick: (HostDevice) -> Unit) {
    val state by mainViewModel.state.collectAsState()
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AnimatedVisibility(
            !state.isLoading && state.reachableDevices.isEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "click on start to find reachable host devices...",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(bottom = 50.dp)) {
            androidx.compose.animation.AnimatedVisibility(
                state.isLoading,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "searching reachable host devices...",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CircularProgressIndicator()
                }
            }
            LazyRow(
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(state.reachableDevices) { hostDevice ->
                    Card(
                        Modifier.size(150.dp).padding(10.dp).onClick {
                            onHostDeviceClick(hostDevice)
                        },
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hostDevice.deviceName.ifBlank { "unknown name" },
                                maxLines = 3,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.7f)
                .padding(bottom = 30.dp),
            onClick = {
                if (!state.isLoading) {
                    mainViewModel.startFinding()
                } else {
                    mainViewModel.stopFinding()
                }
            },
        ) {
            Text(state.startStopButtonText)
        }
    }
}