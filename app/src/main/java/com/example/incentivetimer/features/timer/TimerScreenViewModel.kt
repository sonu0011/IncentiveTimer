package com.example.incentivetimer.features.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val pomodoroTimeManager: PomodoroTimeManager,
    savedStateHandle: SavedStateHandle,

    ) : ViewModel(), TimerScreenActions {

    val pomodoroTimerState = pomodoroTimeManager.pomodoroTimerState.asLiveData()

    private val showResetTimerConfirmationDialogLiveData =
        savedStateHandle.getLiveData("showResetTimerConfirmationDialog", false)
    val showResetTimerConfirmationDialog: LiveData<Boolean> =
        showResetTimerConfirmationDialogLiveData


    private val showResetPomodoroSetConfirmationDialogLiveData =
        savedStateHandle.getLiveData("showResetPomodoroSetConfirmationDialogLiveData", false)
    val showResetPomodoroSetConfirmationDialog: LiveData<Boolean> =
        showResetPomodoroSetConfirmationDialogLiveData


    private val showResetPomodoroCountConfirmationDialogLiveData =
        savedStateHandle.getLiveData("showResetPomodoroCountConfirmationDialogLiveData", false)
    val showResetPomodoroCountConfirmationDialog: LiveData<Boolean> =
        showResetPomodoroCountConfirmationDialogLiveData

    private val showSkipBreakConfirmationDialogLiveData =
        savedStateHandle.getLiveData("showSkipBreakConfirmationDialogLiveData", false)
    val showSkipConfirmationDialog: LiveData<Boolean> =
        showSkipBreakConfirmationDialogLiveData


    override fun onResetTimerClicked() {
        showResetTimerConfirmationDialogLiveData.value = true
    }

    override fun onResetTimerConfirmed() {
        showResetTimerConfirmationDialogLiveData.value = false
        pomodoroTimeManager.resetTimer()
    }

    override fun onResetTimerDialogDismissed() {
        showResetTimerConfirmationDialogLiveData.value = false
    }

    override fun onResetPomodoroSetClicked() {
        showResetPomodoroSetConfirmationDialogLiveData.value = true
    }

    override fun onResetPomodoroSetConfirmed() {
        showResetPomodoroSetConfirmationDialogLiveData.value = false
        pomodoroTimeManager.resetPomodoroSet()
    }

    override fun onResetPomodoroSetDialogDismissed() {
        showResetPomodoroSetConfirmationDialogLiveData.value = false
    }

    override fun startStopTimer() {
        pomodoroTimeManager.startStopTimer()
    }

    override fun onResetPomodoroCountClicked() {
        showResetPomodoroCountConfirmationDialogLiveData.value = true
    }

    override fun onResetPomodoroCountConfirmed() {
        showResetPomodoroCountConfirmationDialogLiveData.value = false
        pomodoroTimeManager.resetPomodoroCount()
    }

    override fun onResetPomodoroCountDialogDismissed() {
        showResetPomodoroCountConfirmationDialogLiveData.value = false
    }

    override fun onSkipBreakClicked() {
        showSkipBreakConfirmationDialogLiveData.value = true
    }

    override fun onSkipBreakConfirmed() {
        showSkipBreakConfirmationDialogLiveData.value = false
        pomodoroTimeManager.skipBreak()
    }

    override fun onSkipBreakDialogDismissed() {
        showSkipBreakConfirmationDialogLiveData.value = false

    }
}