package com.example.incentivetimer.features.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val pomodoroTimeManager: PomodoroTimeManager
) : ViewModel(), TimerScreenActions {

    val timeLeftInMillis = pomodoroTimeManager.timeLeftInMillis.asLiveData()
    val timerRunning = pomodoroTimeManager.timerRunning.asLiveData()
    val currentTimeTargetInMillis = pomodoroTimeManager.currentTimeTargetInMillis.asLiveData()
    val currentPhase = pomodoroTimeManager.currentPhase.asLiveData()
    val pomodorosCompleted = pomodoroTimeManager.pomodorosCompleted.asLiveData()

    override fun onResetTimerClicked() {
    }

    override fun onResetPomodoroSetClicked() {
    }

    override fun startStopTimer() {
        pomodoroTimeManager.startStopTimer()
    }
}