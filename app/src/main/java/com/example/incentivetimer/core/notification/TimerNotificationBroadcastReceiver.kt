package com.example.incentivetimer.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.incentivetimer.features.timer.PomodoroPhase
import com.example.incentivetimer.features.timer.PomodoroTimeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerNotificationBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var pomodoroTimeManager: PomodoroTimeManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        val timerStopped = intent?.getBooleanExtra(EXTRA_TIMER_RUNNING, false)
        pomodoroTimeManager.startStopTimer()
        if (timerStopped == true) {
            val currentPhase = intent.getSerializableExtra(EXTRA_POMODORO_PHASE) as? PomodoroPhase
            val timeLeftInMillis = intent.getLongExtra(EXTRA_TIME_LEFT_IN_MILLIS, -1L)
            if (currentPhase != null && timeLeftInMillis != -1L) {
                notificationHelper.showResumeTimerNotification(
                    timerRunning = false,
                    currentPhase = currentPhase,
                    timeLeftInMillis = timeLeftInMillis
                )
            }
        }

    }
}

const val EXTRA_TIMER_RUNNING = "EXTRA_TIMER_RUNNING"
const val EXTRA_POMODORO_PHASE = "EXTRA_POMODORO_PHASE"
const val EXTRA_TIME_LEFT_IN_MILLIS = "EXTRA_TIME_LEFT_IN_MILLIS"
