package com.example.incentivetimer.features.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val pomodoroTimeManager: PomodoroTimeManager
) : ViewModel(), TimerScreenActions {

    val pomodoroTimerState = pomodoroTimeManager.pomodoroTimerState.asLiveData()

    override fun onResetTimerClicked() {
        pomodoroTimeManager.resetTimer()
    }

    override fun onResetPomodoroSetClicked() {
        pomodoroTimeManager.resetPomodoroSet()
    }

    override fun startStopTimer() {
        pomodoroTimeManager.startStopTimer()
    }

    override fun onResetPomodoroCountClicked() {
        pomodoroTimeManager.resetPomodoroCount()

    }
}