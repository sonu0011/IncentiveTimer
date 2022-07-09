package com.example.incentivetimer.features.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.incentivetimer.core.screenspecs.ScreenSpec

object TimeScreenSpec : ScreenSpec {
    override val navHostRoute: String = "timer"

    @Composable
    override fun TopBar(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: TimerScreenViewModel = hiltViewModel(navBackStackEntry)
        TimerScreenTopBar(actions = viewModel)
    }

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: TimerScreenViewModel = hiltViewModel(navBackStackEntry)
        val pomodoroTimerState by viewModel.pomodoroTimerState.observeAsState()

        TimerScreenContent(
            actions = viewModel,
            pomodoroTimerState = pomodoroTimerState
        )
    }
}