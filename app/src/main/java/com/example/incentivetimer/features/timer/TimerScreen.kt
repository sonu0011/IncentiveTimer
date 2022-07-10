package com.example.incentivetimer.features.timer

import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
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
import com.example.incentivetimer.core.ui.composables.SimpleConfirmationDialog
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.core.ui.theme.PrimaryLightAlpha
import com.example.incentivetimer.features.timer.PomodoroPhase.*

@Composable
fun TimerScreenContent(
    pomodoroTimerState: PomodoroTimerState?,
    showResetTimerConfirmationDialog: Boolean,
    showResetPomodoroSetConfirmationDialog: Boolean,
    showResetPomodoroCountConfirmationDialog: Boolean,
    showSkipConfirmationDialog: Boolean,
    actions: TimerScreenActions,
) {
    val timerRunning = pomodoroTimerState?.timerRunning ?: false
    Scaffold(
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Timer(
                pomodoroTimerState = pomodoroTimerState
            )
            Spacer(modifier = Modifier.height(48.dp))
            TimerStartStopButton(timerRunning = timerRunning, actions = actions)
        }
    }


    if (showResetTimerConfirmationDialog) {
        SimpleConfirmationDialog(
            title = R.string.reset_timer,
            text = R.string.reset_timer_confirmation_message,
            confirmButtonText = R.string.reset_timer,
            dismissAction = actions::onResetTimerDialogDismissed,
            confirmAction = actions::onResetTimerConfirmed,
        )
    }

    if (showResetPomodoroSetConfirmationDialog) {
        SimpleConfirmationDialog(
            title = R.string.reset_pomodoro_set,
            text = R.string.reset_pomodoro_set_confirmation_message,
            confirmButtonText = R.string.reset_pomodoro_set,
            dismissAction = actions::onResetPomodoroSetDialogDismissed,
            confirmAction = actions::onResetPomodoroSetConfirmed,
        )
    }

    if (showResetPomodoroCountConfirmationDialog) {
        SimpleConfirmationDialog(
            title = R.string.reset_pomodoro_count,
            text = R.string.reset_pomodoro_count_confirmation_message,
            confirmButtonText = R.string.reset_pomodoro_count,
            dismissAction = actions::onResetPomodoroCountDialogDismissed,
            confirmAction = actions::onResetPomodoroCountConfirmed,
        )
    }

    if (showSkipConfirmationDialog) {
        SimpleConfirmationDialog(
            title = R.string.skip_break,
            text = R.string.skip_break_confirmation_message,
            confirmButtonText = R.string.skip_break,
            dismissAction = actions::onSkipBreakDialogDismissed,
            confirmAction = actions::onSkipBreakConfirmed,
        )
    }
}

@Composable
private fun Timer(
    pomodoroTimerState: PomodoroTimerState?,
    modifier: Modifier = Modifier,
) {
    val timeLeftInMillis = pomodoroTimerState?.timeLeftInMillis ?: 0L
    val timeTargetInMillis = pomodoroTimerState?.timeTargetInMillis ?: 0L
    val currentPhase = pomodoroTimerState?.currentPhase
    val pomodorosCompletedInset = pomodoroTimerState?.pomodorosCompletedInset ?: 0
    val pomodorosCompletedTotal = pomodoroTimerState?.pomodorosCompletedTotal ?: 0
    val timerRunning = pomodoroTimerState?.timerRunning ?: false
    val pomodorosPerSetTarget = pomodoroTimerState?.pomodorosPerSetTarget ?: 0

    val progress = timeLeftInMillis.toFloat() / timeTargetInMillis.toFloat()
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

        val phaseText =
            if (currentPhase != null) stringResource(currentPhase.readableName).uppercase() else ""
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = phaseText,
                modifier = Modifier.padding(top = 48.dp),
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(4.dp))
            PomodorosCompletedIndicatorRow(
                pomodorosCompletedInSet = pomodorosCompletedInset,
                pomodorosPerSetTarget = pomodorosPerSetTarget,
                currentPhase = currentPhase,
                timerRunning = timerRunning
            )
        }

        Text(
            text = "Total: $pomodorosCompletedTotal",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            style = MaterialTheme.typography.body2
        )

    }
}

@Composable
private fun PomodorosCompletedIndicatorRow(
    pomodorosCompletedInSet: Int,
    pomodorosPerSetTarget: Int,
    timerRunning: Boolean,
    currentPhase: PomodoroPhase?,
    modifier: Modifier = Modifier
) {
    val pomodoroInProgress = timerRunning && currentPhase == POMODORO
    Row(modifier) {
        repeat(pomodorosPerSetTarget) { index ->
            key(index) {
                SinglePomodorosCompletedIndicator(
                    completed = pomodorosCompletedInSet > index,
                    inProgress = pomodoroInProgress && pomodorosCompletedInSet == index
                )
                if (index < pomodorosPerSetTarget - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }

}

@Composable
private fun SinglePomodorosCompletedIndicator(
    completed: Boolean,
    inProgress: Boolean,
    modifier: Modifier = Modifier
) {
    val uncompletedColor = MaterialTheme.colors.primary.copy(alpha = PrimaryLightAlpha)
    val completedColor = MaterialTheme.colors.primary

    val infiniteAnimation = rememberInfiniteTransition()
    val inProgressColor by infiniteAnimation.animateColor(
        initialValue = uncompletedColor,
        targetValue = completedColor,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color =
        when {
            completed -> completedColor
            inProgress -> inProgressColor
            else -> uncompletedColor
        }
    Box(
        modifier = modifier
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
    actions: TimerScreenActions,
    pomodoroTimerState: PomodoroTimerState?,
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
                    if (pomodoroTimerState?.currentPhase?.isBreak == true) {
                        DropdownMenuItem(
                            onClick = {
                                expended = false
                                actions.onSkipBreakClicked()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.skip_break))
                        }
                    }
                    DropdownMenuItem(
                        onClick = {
                            expended = false
                            actions.onResetPomodoroSetClicked()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.reset_pomodoro_set))
                    }
                    DropdownMenuItem(
                        onClick = {
                            expended = false
                            actions.onResetPomodoroCountClicked()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.reset_pomodoro_count))
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
                pomodoroTimerState = null,
                actions = object : TimerScreenActions {
                    override fun startStopTimer() {}
                    override fun onResetTimerClicked() {}
                    override fun onResetPomodoroSetClicked() {}
                    override fun onResetPomodoroCountClicked() {}
                    override fun onResetTimerConfirmed() {}
                    override fun onResetTimerDialogDismissed() {}
                    override fun onResetPomodoroSetConfirmed() {}
                    override fun onResetPomodoroSetDialogDismissed() {}
                    override fun onResetPomodoroCountConfirmed() {}
                    override fun onResetPomodoroCountDialogDismissed() {}
                    override fun onSkipBreakClicked() {}
                    override fun onSkipBreakConfirmed() {}
                    override fun onSkipBreakDialogDismissed() {}
                },
                showResetPomodoroCountConfirmationDialog = true,
                showResetPomodoroSetConfirmationDialog = true,
                showResetTimerConfirmationDialog = true,
                showSkipConfirmationDialog = false
            )
        }
    }
}