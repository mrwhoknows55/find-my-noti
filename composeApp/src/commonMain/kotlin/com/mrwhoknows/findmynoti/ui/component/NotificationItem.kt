package com.mrwhoknows.findmynoti.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrwhoknows.findmynoti.NotificationEntity
import com.mrwhoknows.findmynoti.ui.model.Notification
import com.mrwhoknows.findmynoti.ui.model.toNotification
import com.mrwhoknows.findmynoti.ui.theme.AppTheme
import kotlinx.datetime.Clock
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun NotificationItem(
    modifier: Modifier = Modifier, notification: Notification, onClick: () -> Unit
) = Card(modifier = modifier, onClick = onClick, shape = RoundedCornerShape(4.dp)) {

    Column(
        Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO use app icon to load into image here
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(Modifier.background(Color.Gray, CircleShape).size(10.dp))
                Text(
                    notification.appName.orEmpty().ifBlank { "Unknown App" },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = notification.dateTime,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = notification.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        // TODO add annotated string to expand and collapse
        Text(
            notification.content,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
private fun NotificationItemPreview() = AppTheme {
    NotificationEntity(
        id = 1,
        title = "Notification Title",
        content = "This is notification content",
        packageName = "com.notification.app",
        appName = "App Name",
        timestamp = Clock.System.now().toEpochMilliseconds(),
        imageUrl = "",
    ).toNotification()

}

