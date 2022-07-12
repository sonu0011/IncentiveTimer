package com.example.incentivetimer.features.timer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.incentivetimer.core.notification.NotificationHelper
import com.example.incentivetimer.core.notification.TIMER_SERVICE_NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var pomodoroTimerManager: PomodoroTimeManager

    @Inject
    lateinit var notificationHelper: NotificationHelper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(
            TIMER_SERVICE_NOTIFICATION_ID,
            notificationHelper.getBaseTimerServiceNotification().build()
        )

        serviceScope.launch {
            pomodoroTimerManager.pomodoroTimerState.collect { timerState ->
                notificationHelper.updateTimerServiceNotification(
                    currentPhase = timerState.currentPhase,
                    timeLeftInMillis = timerState.timeLeftInMillis,
                    timerRunning = timerState.timerRunning
                )
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        notificationHelper.removeTimerNotification()
    }

    override fun onBind(p0: Intent?): IBinder? = null

}
