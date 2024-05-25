package com.mrwhoknows.findmynoti.ui.noti

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrwhoknows.findmynoti.server.model.NotificationDTO
import com.mrwhoknows.findmynoti.ui.component.NotificationList
import com.mrwhoknows.findmynoti.ui.component.SearchBar
import com.mrwhoknows.findmynoti.ui.model.toNotification
import io.github.aakira.napier.Napier

@Composable
fun NotificationsScreen(notificationViewModel: NotificationViewModel) {
    val notifications by notificationViewModel.notifications.collectAsState()
    Column(
        Modifier.fillMaxSize().background(colorScheme.surface)
    ) {
        var searchText by remember { mutableStateOf("") }
        SearchBar(
            Modifier.padding(16.dp).fillMaxWidth().background(colorScheme.background), searchText
        ) {
            searchText = it
            notificationViewModel.search(it)
        }
        Box {
            val scrollState = rememberLazyListState()
            NotificationList(
                scrollState = scrollState,
                notifications = notifications.map(NotificationDTO::toNotification)
            ) {
                Napier.i { "onItemClick: $it" }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                style = ScrollbarStyle(
                    minimalHeight = 16.dp,
                    thickness = 8.dp,
                    shape = RoundedCornerShape(4.dp),
                    hoverDurationMillis = 300,
                    unhoverColor = colorScheme.secondary.copy(alpha = 0.5f),
                    hoverColor = colorScheme.secondary
                ),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                ),
            )
        }

    }
}
