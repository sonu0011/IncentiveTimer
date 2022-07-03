package com.example.incentivetimer.core.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.incentivetimer.core.ui.theme.PrimaryLightAlpha


@Composable
fun RoundedCornerCircularProgressBarIndicatorWithBackground(
    /*@FloatRange(from = 0.0, to = 1.0)*/
    progress: Float,
    modifier: Modifier = Modifier,
    foregroundColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.primary.copy(alpha = PrimaryLightAlpha),
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {

    Box(modifier) {
        RoundedCornerCircularProgressBarIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            color = foregroundColor,
            strokeWidth = strokeWidth
        )

        RoundedCornerCircularProgressBarIndicator(
            progress = 1f,
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor,
            strokeWidth = strokeWidth
        )

    }

}