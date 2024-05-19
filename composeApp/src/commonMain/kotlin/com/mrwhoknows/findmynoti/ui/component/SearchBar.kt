package com.mrwhoknows.findmynoti.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onTextChange: (String) -> Unit
) = OutlinedTextField(
    modifier = modifier,
    value = searchText,
    singleLine = true,
    onValueChange = onTextChange,
    label = {
        Text("Search", color = MaterialTheme.colorScheme.onSurface)
    },
    leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
)