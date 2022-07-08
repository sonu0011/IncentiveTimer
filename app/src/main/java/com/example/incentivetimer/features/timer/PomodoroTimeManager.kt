package com.example.incentivetimer.features.timer

import android.os.CountDownTimer
import com.example.incentivetimer.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class PomodoroPhase {
    POMODORO, SHORT_BREAK, LONG_BREAK
}

const val POMODORO_DURATION_IN_MILLS =/* 25 * 60 * 1_000L */ 10000L
private const val SHORT_BREAK_DURATION_IN_MILLS = /* 5 * 60 * 1_000L */ 3000L
private const val LONG_BREAK_DURATION_IN_MILLS = /*15 * 60 * 1_000L */ 6000L
private const val POMODOROS_PER_SET = 4

@Singleton
class PomodoroTimeManager @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
) {
    private val currentPhaseFlow = MutableStateFlow(PomodoroPhase.POMODORO)
    val currentPhase: Flow<PomodoroPhase> = currentPhaseFlow

    private val currentTimeTargetInMillisFlow = MutableStateFlow(POMODORO_DURATION_IN_MILLS)
    val currentTimeTargetInMillis: Flow<Long> = currentTimeTargetInMillisFlow


    private val pomodorosCompletedFlow = MutableStateFlow(0)
    val pomodorosCompleted: Flow<Int> = pomodorosCompletedFlow

    private val pomodorosTargetFlow = MutableStateFlow(POMODOROS_PER_SET)
    val pomodorosTarget: Flow<Int> = pomodorosTargetFlow

    private var countDownTimer: CountDownTimer? = null

    private val timeLeftInMillsFlow = MutableStateFlow(POMODORO_DURATION_IN_MILLS)
    val timeLeftInMillis: Flow<Long> = timeLeftInMillsFlow

    private val timerRunningFlow = MutableStateFlow(false)
    val timerRunning: Flow<Boolean> = timerRunningFlow

    fun startStopTimer() {
        val timerRunning = timerRunningFlow.value
        if (timerRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        resetPomodoroCounterIfTragetReached()
        val timeLeftInMillis = timeLeftInMillsFlow.value
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000L) {
            override fun onTick(millisUntillFinished: Long) {
                timeLeftInMillsFlow.value = millisUntillFinished
            }

            override fun onFinish() {
                stopTimer()
                startNextPhase()
            }
        }.start()
        timerRunningFlow.value = true
    }


    private fun stopTimer() {
        countDownTimer?.cancel()
        timerRunningFlow.value = false
    }

    private fun startNextPhase() {
        val pastPhase = currentPhaseFlow.value
        val pomodorosCompleted =
            if (pastPhase == PomodoroPhase.POMODORO) pomodorosCompletedFlow.value + 1 else pomodorosCompletedFlow.value
        val pomodorosTarget = pomodorosTargetFlow.value
        pomodorosCompletedFlow.value = pomodorosCompleted

        val nextPhase = getNextPhase(pastPhase, pomodorosCompleted, pomodorosTarget)

        currentPhaseFlow.value = nextPhase
        val nextTimeTarget = getTimeTargetForNextPhase(nextPhase)
        currentTimeTargetInMillisFlow.value = getTimeTargetForNextPhase(nextPhase)
        timeLeftInMillsFlow.value = nextTimeTarget
        startTimer()
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

    private fun resetPomodoroCounterIfTragetReached() {
        val currentPhase = currentPhaseFlow.value
        val pomodorosCompleted = pomodorosCompletedFlow.value
        val pomodorosTarget = pomodorosTargetFlow.value

        if (pomodorosCompleted >= pomodorosTarget && currentPhase == PomodoroPhase.POMODORO) {
            pomodorosCompletedFlow.value = 0
        }
    }


}