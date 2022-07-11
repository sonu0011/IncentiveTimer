package com.example.incentivetimer.features.timer

import android.os.CountDownTimer
import androidx.annotation.StringRes
import com.example.incentivetimer.R
import com.example.incentivetimer.core.notification.NotificationHelper
import com.example.incentivetimer.di.ApplicationScope
import com.example.incentivetimer.features.rewards.RewardUnlockManager
import com.zhuinden.flowcombinetuplekt.combineTuple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class PomodoroTimerState(
    val timerRunning: Boolean,
    val currentPhase: PomodoroPhase,
    val timeLeftInMillis: Long,
    val timeTargetInMillis: Long,
    val pomodorosCompletedInset: Int,
    val pomodorosPerSetTarget: Int,
    val pomodorosCompletedTotal: Int,

    )

enum class PomodoroPhase(@StringRes val readableName: Int) {
    POMODORO(R.string.pomodoro), SHORT_BREAK(R.string.short_break), LONG_BREAK(R.string.long_break);

    val isBreak get() = this == SHORT_BREAK || this == LONG_BREAK
}

const val POMODORO_DURATION_IN_MILLS =/* 25 * 60 * 1_000L */ 10000L
private const val SHORT_BREAK_DURATION_IN_MILLS = /* 5 * 60 * 1_000L */ 3000L
private const val LONG_BREAK_DURATION_IN_MILLS = /*15 * 60 * 1_000L */ 6000L
private const val POMODOROS_PER_SET = 4

@Singleton
class PomodoroTimeManager @Inject constructor(
    private val timerServiceManager: TimerServiceManager,
    private val notificationHelper: NotificationHelper,
    private val rewardUnlockManager: RewardUnlockManager
) {
    private val timerRunningFlow = MutableStateFlow(false)
    private val currentPhaseFlow = MutableStateFlow(PomodoroPhase.POMODORO)
    private val timeLeftInMillsFlow = MutableStateFlow(POMODORO_DURATION_IN_MILLS)
    private val timeTargetInMillisFlow = MutableStateFlow(POMODORO_DURATION_IN_MILLS)
    private val pomodorosCompletedInsetFlow = MutableStateFlow(0)
    private val pomodorosPerSetTargetFlow = MutableStateFlow(POMODOROS_PER_SET)
    private val pomodorosCompletedTotalFlow = MutableStateFlow(0)

    val pomodoroTimerState = combineTuple(
        timerRunningFlow,
        currentPhaseFlow,
        timeLeftInMillsFlow,
        timeTargetInMillisFlow,
        pomodorosCompletedInsetFlow,
        pomodorosPerSetTargetFlow,
        pomodorosCompletedTotalFlow
    ).map { (timerRunningFlow, currentPhaseFlow, timeLeftInMillsFlow, timeTargetInMillisFlow, pomodorosCompletedInsetFlow, pomodorosPerSetTargetFlow, pomodorosCompletedTotalFlow) ->
        PomodoroTimerState(
            timerRunning = timerRunningFlow,
            currentPhase = currentPhaseFlow,
            timeLeftInMillis = timeLeftInMillsFlow,
            timeTargetInMillis = timeTargetInMillisFlow,
            pomodorosCompletedInset = pomodorosCompletedInsetFlow,
            pomodorosPerSetTarget = pomodorosPerSetTargetFlow,
            pomodorosCompletedTotal = pomodorosCompletedTotalFlow
        )
    }

    private var countDownTimer: CountDownTimer? = null

    fun startStopTimer() {
        notificationHelper.removeTimerCompletedNotification()
        val timerRunning = timerRunningFlow.value
        if (timerRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        resetPomodoroCounterIfTargetReached()
        val timeLeftInMillis = timeLeftInMillsFlow.value
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000L) {
            override fun onTick(millisUntillFinished: Long) {
                timeLeftInMillsFlow.value = millisUntillFinished
            }

            override fun onFinish() {
                val currentPhase = currentPhaseFlow.value
                notificationHelper.showTimerCompletedNotification(currentPhase)
                if (currentPhase == PomodoroPhase.POMODORO) {
                    pomodorosCompletedTotalFlow.value++
                    pomodorosCompletedInsetFlow.value++
                    rewardUnlockManager.rollAllRewards()
                }
                startNextPhase()
                startTimer()
            }
        }.start()
        timerRunningFlow.value = true
        timerServiceManager.startTimerService()
    }

    fun skipBreak() {
        if (currentPhaseFlow.value.isBreak) {
            countDownTimer?.cancel()
            startNextPhase()
            if (timerRunningFlow.value) {
                startTimer()
            }
        }

    }


    private fun stopTimer() {
        countDownTimer?.cancel()
        timerRunningFlow.value = false
        timerServiceManager.stopTimerService()
    }

    private fun startNextPhase() {
        val pastPhase = currentPhaseFlow.value
        val pomodorosCompleted = pomodorosCompletedInsetFlow.value
        val pomodorosTarget = pomodorosPerSetTargetFlow.value
        pomodorosCompletedInsetFlow.value = pomodorosCompleted
        setPomodoroPhase(getNextPhase(pastPhase, pomodorosCompleted, pomodorosTarget))

    }

    private fun setPomodoroPhase(nextPhase: PomodoroPhase) {
        currentPhaseFlow.value = nextPhase
        val nextTimeTarget = getTimeTargetForNextPhase(nextPhase)
        timeTargetInMillisFlow.value = nextTimeTarget
        timeLeftInMillsFlow.value = nextTimeTarget
    }

    private fun getNextPhase(
        pastPhase: PomodoroPhase,
        pomodorosCompleted: Int,
        pomodorosTarget: Int
    ) = when (pastPhase) {
        PomodoroPhase.POMODORO -> {
            if (pomodorosCompleted >= pomodorosTarget) PomodoroPhase.LONG_BREAK else PomodoroPhase.SHORT_BREAK
        }
        PomodoroPhase.SHORT_BREAK -> {
            PomodoroPhase.POMODORO
        }
        PomodoroPhase.LONG_BREAK -> {
            PomodoroPhase.POMODORO
        }
    }

    private fun getTimeTargetForNextPhase(phase: PomodoroPhase) = when (phase) {
        PomodoroPhase.POMODORO -> POMODORO_DURATION_IN_MILLS
        PomodoroPhase.SHORT_BREAK -> SHORT_BREAK_DURATION_IN_MILLS
        PomodoroPhase.LONG_BREAK -> LONG_BREAK_DURATION_IN_MILLS
    }

    private fun resetPomodoroCounterIfTargetReached() {
        val currentPhase = currentPhaseFlow.value
        val pomodorosCompleted = pomodorosCompletedInsetFlow.value
        val pomodorosTarget = pomodorosPerSetTargetFlow.value

        if (pomodorosCompleted >= pomodorosTarget && currentPhase == PomodoroPhase.POMODORO) {
            pomodorosCompletedInsetFlow.value = 0
        }
    }

    fun resetTimer() {
        stopTimer()
        timeLeftInMillsFlow.value = timeTargetInMillisFlow.value
    }

    fun resetPomodoroSet() {
        resetTimer()
        pomodorosCompletedInsetFlow.value = 0
        setPomodoroPhase(PomodoroPhase.POMODORO)
    }

    fun resetPomodoroCount() {
        pomodorosCompletedTotalFlow.value = 0
    }


}