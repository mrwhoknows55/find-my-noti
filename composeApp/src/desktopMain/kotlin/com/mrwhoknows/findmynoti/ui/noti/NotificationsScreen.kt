package com.mrwhoknows.findmynoti.ui.noti

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrwhoknows.findmynoti.ui.component.NotificationsList
import com.mrwhoknows.findmynoti.ui.component.SearchBar

@Composable
fun NotificationsScreen(notificationViewModel: NotificationViewModel) {
    val notifications by notificationViewModel.notifications.collectAsState()
    Column(
        Modifier.fillMaxSize().background(colorScheme.surface)
    ) {
        var searchText by remember { mutableStateOf("") }
        SearchBar(
            Modifier.padding(16.dp).fillMaxWidth()
                .background(colorScheme.background),
            searchText
        ) {
            searchText = it
            notificationViewModel.search(it)
        }
        NotificationsList(notifications = notifications)
    }
}
