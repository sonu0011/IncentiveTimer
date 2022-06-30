package com.example.incentivetimer.core.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ITIconButton(
    modifier: Modifier = Modifier,
    onclick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val iconButtonBackground = if (isSystemInDarkTheme()) Color.Gray else Color.LightGray
    IconButton(
        onClick = onclick,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(iconButtonBackground)
    ) {
        content()
    }

}