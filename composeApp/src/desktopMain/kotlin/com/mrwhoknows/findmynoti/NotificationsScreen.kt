package com.mrwhoknows.findmynoti

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationsScreen(notificationViewModel: NotificationViewModel) {
    val notifications by notificationViewModel.notifications.collectAsState()
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp)) {
            stickyHeader {
                var searchText by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchText,
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            "search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    singleLine = true,
                    label = {
                        Text("search notification", color = MaterialTheme.colorScheme.onSurface)
                    },
                    onValueChange = {
                        searchText = it
                        notificationViewModel.search(it)
                    }
                )
                Spacer(Modifier.size(20.dp))
            }
            items(notifications, key = { it.id }) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = it.toString(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

