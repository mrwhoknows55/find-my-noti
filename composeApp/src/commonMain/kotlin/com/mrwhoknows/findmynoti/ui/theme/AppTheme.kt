package com.mrwhoknows.findmynoti.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.rememberDynamicColorScheme

@Composable
fun AppTheme(
    seedColor: Color = Color(0xFF276389),
    content: @Composable () -> Unit
) {
    val colorScheme = rememberDynamicColorScheme(
        seedColor,
        isSystemInDarkTheme()
    )
    MaterialTheme(colorScheme = colorScheme) {
        content()
    }
}