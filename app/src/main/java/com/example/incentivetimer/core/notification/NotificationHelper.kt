package com.example.incentivetimer.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.incentivetimer.R
import com.example.incentivetimer.application.ITActivity
import com.example.incentivetimer.features.timer.PomodoroPhase
import com.example.incentivetimer.features.timer.PomodoroTimerState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {
    private var notificationManager = NotificationManagerCompat.from(applicationContext)
    private val mutabilityFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    private val openActivityIntent = Intent(applicationContext, ITActivity::class.java)
    private val openActivityPendingIntent =
        PendingIntent.getActivity(
            applicationContext,
            0,
            openActivityIntent,
            mutabilityFlag
        )

    init {
        createNotificationChannel()
    }

    fun getBaseTimerServiceNotification() =
        NotificationCompat.Builder(applicationContext, TIMER_SERVICE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setSilent(true)
            .setContentIntent(openActivityPendingIntent)
            .setOnlyAlertOnce(true)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timerServiceChannel = NotificationChannel(
                TIMER_SERVICE_CHANNEL_ID,
                applicationContext.getString(R.string.timer_service_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT

            )
            timerServiceChannel.apply {
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(timerServiceChannel)
        }
    }

    fun showTimerCompletedNotification(currentPhase: PomodoroPhase) {
        val title: Int
        val text: Int

        when (currentPhase) {
            PomodoroPhase.POMODORO -> {
                title = R.string.pomodoro_completed_title
                text = R.string.pomodoro_completed_message
            }
            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
                title = R.string.break_over_title
                text = R.string.break_over_message
            }
        }
        val timerCompletionNotification =
            NotificationCompat.Builder(applicationContext, TIMER_SERVICE_CHANNEL_ID)
                .setContentTitle(applicationContext.getString(title))
                .setContentText(applicationContext.getString(text))
                .setSmallIcon(R.drawable.ic_timer)
                .build()
        notificationManager.notify(TIMER_COMPLETED_NOTIFICATION_ID, timerCompletionNotification)
    }

    fun updateTimerNotification(timerState: PomodoroTimerState) {
        val notificationUpdate = getBaseTimerServiceNotification()
            .setContentTitle(applicationContext.getString(timerState.currentPhase.readableName))
            .setContentText(timerState.timeLeftInMillis.toString())
            .build()
        notificationManager.notify(TIMER_SERVICE_NOTIFICATION_ID, notificationUpdate)
    }

    fun removeTimerNotification() {
        notificationManager.cancel(TIMER_SERVICE_NOTIFICATION_ID)
    }

    fun removeTimerCompletedNotification() {
        notificationManager.cancel(TIMER_COMPLETED_NOTIFICATION_ID)
    }


}

private const val TIMER_SERVICE_CHANNEL_ID = "timer_service_channel"
const val TIMER_SERVICE_NOTIFICATION_ID = 123
private const val TIMER_COMPLETED_NOTIFICATION_ID = 124