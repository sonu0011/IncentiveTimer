package com.example.incentivetimer.features.timer

interface TimerScreenActions {
    fun startStopTimer()
    fun onResetTimerClicked()
    fun onResetTimerConfirmed()
    fun onResetTimerDialogDismissed()
    fun onResetPomodoroSetClicked()
    fun onResetPomodoroSetConfirmed()
    fun onResetPomodoroSetDialogDismissed()
    fun onResetPomodoroCountClicked()
    fun onResetPomodoroCountConfirmed()
    fun onResetPomodoroCountDialogDismissed()
}