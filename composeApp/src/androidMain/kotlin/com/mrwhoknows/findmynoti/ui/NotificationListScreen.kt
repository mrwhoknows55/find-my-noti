package com.mrwhoknows.findmynoti.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mrwhoknows.findmynoti.ui.component.NotificationList
import com.mrwhoknows.findmynoti.ui.component.SearchBar
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import io.github.aakira.napier.Napier

class NotificationListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        AppTheme {
            val screenModel = koinScreenModel<NotificationListScreenModel>()

            val notifications = screenModel.notifications.collectAsLazyPagingItems()
            Column(
                Modifier
                    .statusBarsPadding()
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FindMyNotification",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton({
                        // TODO
                        navigator.push(SettingsScreen())
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "settings"
                        )
                    }
                }


                SearchBar(
                    Modifier.padding(16.dp).fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    screenModel.search(it)
                }

                NotificationList(
                    modifier = Modifier
                        .padding(horizontal = 6.dp).fillMaxSize(),
                    notifications = notifications
                ) {
                    // TODO
                    Napier.i { "clicked on $it" }
                }
            }
        }
    }
}
