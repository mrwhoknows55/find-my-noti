package com.mrwhoknows.findmynoti.ui.component

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrwhoknows.findmynoti.data.model.NotificationDTO


@Composable
fun NotificationsList(
    notifications: List<NotificationDTO>,
    modifier: Modifier = Modifier,
) = Box(modifier = modifier) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = PaddingValues(20.dp)
    ) {
        items(notifications, key = { it.id }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer)
            ) {
                Column(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = it.title.ifBlank { "Untitled" },
                        color = colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2
                    )
                    Text(
                        text = it.content.ifBlank { "Empty Body" },
                        color = colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(Modifier.size(8.dp))
        }
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