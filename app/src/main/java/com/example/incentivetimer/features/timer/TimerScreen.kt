package com.example.incentivetimer.features.timer

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.incentivetimer.R
import com.example.incentivetimer.core.ui.composables.RoundedCornerCircularProgressBarIndicatorWithBackground
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.core.ui.theme.PrimaryLightAlpha
import com.example.incentivetimer.features.timer.PomodoroPhase.*

@Composable
fun TimerScreenContent(
    timerRunning: Boolean,
    timeLeftInMillis: Long,
    actions: TimerScreenActions,
    currentTimeTargetInMillis: Long,
    pomodorosCompleted: Int,
    currentPhase: PomodoroPhase?,
) {
    Scaffold(
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Timer(
                timeLeftInMillis = timeLeftInMillis,
                currentTimeTargetInMillis = currentTimeTargetInMillis,
                currentPhase = currentPhase,
                pomodorosCompleted = pomodorosCompleted
            )
            Spacer(modifier = Modifier.height(48.dp))
            TimerStartStopButton(timerRunning = timerRunning, actions = actions)
        }
    }
}

@Composable
private fun Timer(
    timeLeftInMillis: Long,
    currentTimeTargetInMillis: Long,
    currentPhase: PomodoroPhase?,
    pomodorosCompleted: Int,
    modifier: Modifier = Modifier,
) {
    val progress = timeLeftInMillis.toFloat() / currentTimeTargetInMillis.toFloat()
    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        RoundedCornerCircularProgressBarIndicatorWithBackground(
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleX = -1f, scaleY = 1f),
            strokeWidth = 16.dp
        )

        Text(
            text = timeLeftInMillis.toString(),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.align(Alignment.Center)
        )


        val phaseText = when (currentPhase) {
            POMODORO -> stringResource(id = R.string.pomodoro).uppercase()
            SHORT_BREAK -> stringResource(id = R.string.short_break).uppercase()
            LONG_BREAK -> stringResource(id = R.string.long_break).uppercase()
            null -> stringResource(id = R.string.empty_string)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = phaseText, modifier = Modifier.padding(top = 48.dp))
            Spacer(modifier = Modifier.height(4.dp))
            PomodorosCompletedIndicatorRow(pomodorosCompleted = pomodorosCompleted)
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
    modifier: Modifier = Modifier,
    timerRunning: Boolean,
    actions: TimerScreenActions,
) {
    val startStopIcon = if (!timerRunning) Icons.Default.PlayArrow else Icons.Default.Pause
    val contentDescription = if (!timerRunning) R.string.start_timer else R.string.stop_timer
    FloatingActionButton(
        onClick = {
            actions.startStopTimer()
        }, modifier = modifier.size(64.dp)
    ) {
        Icon(
            imageVector = startStopIcon,
            contentDescription = stringResource(id = contentDescription),
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }

}

@Composable
fun TimerScreenTopBar(
    actions: TimerScreenActions
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.timer))
        },
        actions = {
            var expended by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expended = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.open_menu)
                    )
                }
                DropdownMenu(
                    expanded = expended,
                    onDismissRequest = { expended = false }) {
                    DropdownMenuItem(
                        onClick = {
                            expended = false
                            actions.onResetTimerClicked()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.reset_timer))
                    }
                    DropdownMenuItem(
                        onClick = {
                            expended = false
                            actions.onResetPomodoroSetClicked()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.reset_pomodoro_set))
                    }
                }

            }
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
            TimerScreenContent(
                true,
                actions = object : TimerScreenActions {
                    override fun startStopTimer() {}
                    override fun onResetTimerClicked() {}
                    override fun onResetPomodoroSetClicked() {}
                },
                timeLeftInMillis = POMODORO_DURATION_IN_MILLS,
                currentTimeTargetInMillis = POMODORO_DURATION_IN_MILLS,
                currentPhase = null,
                pomodorosCompleted = 3
            )
        }
    }
}