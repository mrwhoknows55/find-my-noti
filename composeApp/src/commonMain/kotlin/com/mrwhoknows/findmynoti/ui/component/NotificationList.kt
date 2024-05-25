package com.mrwhoknows.findmynoti.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.ui.model.Notification
import com.mrwhoknows.findmynoti.ui.model.toNotification
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import kotlinx.datetime.Clock
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit


@Composable
fun NotificationList(
    modifier: Modifier = Modifier.fillMaxSize(),
    scrollState: LazyListState = rememberLazyListState(),
    notifications: List<Notification>,
    onItemClick: (Notification) -> Unit
) = LazyColumn(
    modifier = modifier,
    state = scrollState,
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(10.dp)
) {

    items(notifications, key = {
        it.id
    }) { item ->
        NotificationItem(notification = item) {
            onItemClick(item)
        }
    }

    item {
        Box(modifier = Modifier.navigationBarsPadding())
    }
}

@Preview
@Composable
private fun NotificationListPreview() = AppTheme {
    NotificationList(modifier = Modifier.fillMaxSize(), notifications = (1..20).map {
        NotificationEntity(
            id = it.toLong(),
            title = "Notification Title $it",
            content = "This is notification content $it".repeat(it.minus(13).coerceAtLeast(1)),
            packageName = "com.notification.app$it",
            appName = "App Name $it",
            timestamp = Clock.System.now().toEpochMilliseconds()
                .minus(it.plus(140).days.toLong(DurationUnit.MILLISECONDS)),
            imageUrl = "",
        ).toNotification()
    }) {

    }
}
