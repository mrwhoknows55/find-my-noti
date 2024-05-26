package com.mrwhoknows.findmynoti.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search notification by title or content",
    onTextChange: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = modifier,
        value = searchText,
        singleLine = true,
        onValueChange = {
            searchText = it
            onTextChange(it)
        },
        label = {
            Text(hint, color = MaterialTheme.colorScheme.onSurface)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = hint,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    )
}