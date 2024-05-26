package com.mrwhoknows.findmynoti.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import com.mrwhoknows.findmynoti.ui.model.Notification


@Composable
fun NotificationList(
    modifier: Modifier = Modifier.fillMaxSize(),
    scrollState: LazyListState = rememberLazyListState(),
    notifications: LazyPagingItems<Notification>,
    onItemClick: (Notification) -> Unit
) = LazyColumn(
    modifier = modifier,
    state = scrollState,
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(10.dp)
) {

    items(notifications.itemCount, key = {
        notifications[it]!!.id
    }) { index ->
        notifications[index]?.let { item ->
            NotificationItem(notification = item) {
                onItemClick(item)
            }
        }
    }

    item {
        Box(modifier = Modifier.navigationBarsPadding())
    }
}
