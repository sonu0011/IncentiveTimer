package com.example.incentivetimer.features.timer

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.incentivetimer.R
import com.example.incentivetimer.core.ui.composables.RoundedCornerCircularProgressBarIndicatorWithBackground
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.core.ui.theme.PrimaryLightAlpha

@Composable
fun TimerScreen(
    navController: NavController
) {
    ScreenContent(timerRunning = true)
}

@Composable
private fun ScreenContent(
    timerRunning: Boolean
) {
    Scaffold(
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Timer()
            Spacer(modifier = Modifier.height(48.dp))
            TimerStartStopButton(timerRunning)
        }
    }
}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        RoundedCornerCircularProgressBarIndicatorWithBackground(
            progress = 0.6f,
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleX = -1f, scaleY = 1f),
            strokeWidth = 16.dp
        )
        Box {
            Text(
                text = "25:00",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.align(Alignment.Center)
            )
            PomodorosCompletedIndicatorRow(
                pomodorosCompleted = 3,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(top = 60.dp)
            )
        }

    }
}

@Composable
private fun PomodorosCompletedIndicatorRow(
    pomodorosCompleted: Int,
    modifier: Modifier = Modifier
) {

    Row(modifier) {
        SinglePomodorosCompletedIndicator(completed = pomodorosCompleted > 0)
        Spacer(modifier = Modifier.width(4.dp))
        SinglePomodorosCompletedIndicator(completed = pomodorosCompleted > 1)
        Spacer(modifier = Modifier.width(4.dp))
        SinglePomodorosCompletedIndicator(completed = pomodorosCompleted > 2)
        Spacer(modifier = Modifier.width(4.dp))
        SinglePomodorosCompletedIndicator(completed = pomodorosCompleted > 3)
        Spacer(modifier = Modifier.width(4.dp))
    }

}

@Composable
private fun SinglePomodorosCompletedIndicator(
    completed: Boolean,
    modifier: Modifier = Modifier
) {
    val color =
        if (!completed) MaterialTheme.colors.primary.copy(alpha = PrimaryLightAlpha) else MaterialTheme.colors.primary
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(8.dp)
            .background(color)
    )

}

@Composable
private fun TimerStartStopButton(
    timerRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val contentDescription = if (!timerRunning) R.string.start_timer else R.string.stop_timer
    FloatingActionButton(onClick = { }, modifier = modifier.size(64.dp)) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = stringResource(id = contentDescription),
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }

}

@Composable
fun TimerScreenTopBar() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.timer))
        }
    )
}

@Preview(
    showBackground = false,
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)

@Preview(
    showBackground = true,
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DefaultPreview() {
    IncentiveTimerTheme {
        Surface() {
            ScreenContent(true)
        }
    }
}