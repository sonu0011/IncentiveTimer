package com.example.incentivetimer.features.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import com.example.incentivetimer.core.screenspecs.ScreenSpec

object TimeScreenSpec : ScreenSpec {
    override val navHostRoute: String = "timer"
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink {
            uriPattern = "https://www.incentivetimer.com/timer"
        }
    )

    @Composable
    override fun TopBar(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: TimerScreenViewModel = hiltViewModel(navBackStackEntry)
        val pomodoroTimerState by viewModel.pomodoroTimerState.observeAsState()
        TimerScreenTopBar(
            actions = viewModel,
            pomodoroTimerState = pomodoroTimerState
        )
    }

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: TimerScreenViewModel = hiltViewModel(navBackStackEntry)
        val pomodoroTimerState by viewModel.pomodoroTimerState.observeAsState()
        val showResetTimerConfirmationDialog by viewModel.showResetTimerConfirmationDialog.observeAsState(
            false
        )
        val showResetPomodoroSetConfirmationDialog by viewModel.showResetPomodoroSetConfirmationDialog.observeAsState(
            false
        )
        val showResetPomodoroCountConfirmationDialog by viewModel.showResetPomodoroCountConfirmationDialog.observeAsState(
            false
        )
        val showSkipConfirmationDialog by viewModel.showSkipConfirmationDialog.observeAsState(false)

        TimerScreenContent(
            actions = viewModel,
            pomodoroTimerState = pomodoroTimerState,
            showResetTimerConfirmationDialog = showResetTimerConfirmationDialog,
            showResetPomodoroSetConfirmationDialog = showResetPomodoroSetConfirmationDialog,
            showResetPomodoroCountConfirmationDialog = showResetPomodoroCountConfirmationDialog,
            showSkipConfirmationDialog = showSkipConfirmationDialog,
        )
    }
}