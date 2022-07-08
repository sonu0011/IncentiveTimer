package com.example.incentivetimer.features.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.incentivetimer.R
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
        val timeLeftInMillis by viewModel.timeLeftInMillis.observeAsState(0L)
        val timerRunning by viewModel.timerRunning.observeAsState(false)
        val currentTimeTargetInMillis by viewModel.currentTimeTargetInMillis.observeAsState(0L)
        val currentPhase by viewModel.currentPhase.observeAsState()
        val pomodorosCompleted by viewModel.pomodorosCompleted.observeAsState(0)

        TimerScreenContent(
            timerRunning = timerRunning,
            actions = viewModel,
            timeLeftInMillis = timeLeftInMillis,
            currentTimeTargetInMillis = currentTimeTargetInMillis,
            pomodorosCompleted =pomodorosCompleted,
            currentPhase =currentPhase
        )
    }
}