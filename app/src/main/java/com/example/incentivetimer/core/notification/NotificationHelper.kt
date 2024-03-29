package com.example.incentivetimer.core.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.incentivetimer.R
import com.example.incentivetimer.application.ITActivity
import com.example.incentivetimer.data.Reward
import com.example.incentivetimer.features.timer.PomodoroPhase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {
    // TODO: navigation is not working with launch mode as single top
    private var notificationManager = NotificationManagerCompat.from(applicationContext)
    private val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    private val openTimerIntent =
        Intent(
            Intent.ACTION_VIEW,
            "https://www.incentivetimer.com/timer".toUri(),
            applicationContext,
            ITActivity::class.java
        )

    private val openRewardListIntent =
        Intent(
            Intent.ACTION_VIEW,
            "https://www.incentivetimer.com/reward_list".toUri(),
            applicationContext,
            ITActivity::class.java
        )
    private val openRewardListPendingIntent =
        PendingIntent.getActivity(
            applicationContext,
            0,
            openRewardListIntent,
            pendingIntentFlags
        )


    private val openTimerPendingIntent =
        PendingIntent.getActivity(
            applicationContext,
            0,
            openTimerIntent,
            pendingIntentFlags
        )

    init {
        createNotificationChannels()
    }

    fun getBaseTimerServiceNotification() =
        NotificationCompat.Builder(applicationContext, TIMER_SERVICE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setSilent(true)
            .setContentIntent(openTimerPendingIntent)
            .setOnlyAlertOnce(true)

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
            NotificationCompat.Builder(applicationContext, TIMER_COMPLETED_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(applicationContext.getString(title))
                .setContentText(applicationContext.getString(text))
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .build()
        notificationManager.notify(TIMER_COMPLETED_NOTIFICATION_ID, timerCompletionNotification)
    }

    fun showNotificationWhenRewardIsUnlocked(reward: Reward) {
        val rewardUnlockedNotification =
            NotificationCompat.Builder(applicationContext, TIMER_COMPLETED_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(applicationContext.getString(R.string.reward_unlocked))
                .setContentText(reward.name)
                .setSmallIcon(R.drawable.ic_star)
                .setContentIntent(openRewardListPendingIntent)
                .setAutoCancel(true)
                .build()
        notificationManager.notify(reward.id.toInt(), rewardUnlockedNotification)
    }

    fun updateTimerServiceNotification(
        timerRunning: Boolean,
        currentPhase: PomodoroPhase,
        timeLeftInMillis: Long
    ) {
        val actionIntent =
            getTimerNotificationActionBroadcast(timerRunning, currentPhase, timeLeftInMillis)

        val notificationUpdate = getBaseTimerServiceNotification()
            .setContentTitle(applicationContext.getString(currentPhase.readableName))
            .setContentText(timeLeftInMillis.toString())
            .addAction(
                R.drawable.ic_pause,
                applicationContext.getString(R.string.pause),
                actionIntent
            )
            .build()
        notificationManager.notify(TIMER_SERVICE_NOTIFICATION_ID, notificationUpdate)
    }


    fun showResumeTimerNotification(
        timerRunning: Boolean,
        currentPhase: PomodoroPhase,
        timeLeftInMillis: Long
    ) {
        val actionIntent =
            getTimerNotificationActionBroadcast(timerRunning, currentPhase, timeLeftInMillis)

        val title = applicationContext.getString(currentPhase.readableName) +
                " (" + applicationContext.getString(R.string.paused) + ")"

        val notificationUpdate = getBaseTimerServiceNotification()
            .setContentTitle(title)
            .setContentText(timeLeftInMillis.toString())
            .addAction(
                R.drawable.ic_resume,
                applicationContext.getString(R.string.resume),
                actionIntent
            )
            .build()
        notificationManager.notify(TIMER_RESUME_NOTIFICATION_ID, notificationUpdate)
    }

    private fun getTimerNotificationActionBroadcast(
        timerRunning: Boolean,
        currentPhase: PomodoroPhase,
        timeLeftInMillis: Long
    ): PendingIntent {
        val broadcastIntent =
            Intent(applicationContext, TimerNotificationBroadcastReceiver::class.java).apply {
                putExtra(EXTRA_TIMER_RUNNING, timerRunning)
                putExtra(EXTRA_POMODORO_PHASE, currentPhase)
                putExtra(EXTRA_TIME_LEFT_IN_MILLIS, timeLeftInMillis)
            }
        return PendingIntent.getBroadcast(
            applicationContext,
            0,
            broadcastIntent,
            pendingIntentFlags
        )
    }


    fun removeTimerNotification() {
        notificationManager.cancel(TIMER_SERVICE_NOTIFICATION_ID)
    }

    fun removeTimerCompletedNotification() {
        notificationManager.cancel(TIMER_COMPLETED_NOTIFICATION_ID)
    }

    fun removeResumeTimerNotification() {
        notificationManager.cancel(TIMER_RESUME_NOTIFICATION_ID)
    }

    private fun createNotificationChannels() {

        val timerServiceChannel = NotificationChannelCompat.Builder(
            TIMER_SERVICE_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setName(applicationContext.getString(R.string.timer_service_channel_name))
            .setDescription(applicationContext.getString(R.string.timer_service_channel_desc))
            .setSound(null, null)
            .build()

        val timerCompletedChannel = NotificationChannelCompat.Builder(
            TIMER_COMPLETED_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(applicationContext.getString(R.string.timer_completed_channel_name))
            .setDescription(applicationContext.getString(R.string.timer_completed_channel_desc))
            .build()

        val rewardUnlockedChannel = NotificationChannelCompat.Builder(
            REWARD_UNLOCKED_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(applicationContext.getString(R.string.reward_unlocked_channel_name))
            .setDescription(applicationContext.getString(R.string.reward_unlocked_channel_desc))
            .build()

        notificationManager.createNotificationChannelsCompat(
            listOf(
                timerServiceChannel,
                timerCompletedChannel,
                rewardUnlockedChannel
            )
        )
    }


}

private const val TIMER_SERVICE_NOTIFICATION_CHANNEL_ID = "timer_service_notification_channel"
private const val TIMER_COMPLETED_NOTIFICATION_CHANNEL_ID = "timer_completed_notification_channel"
private const val REWARD_UNLOCKED_NOTIFICATION_CHANNEL_ID = "reward_unlocked_notification_channel"
const val TIMER_SERVICE_NOTIFICATION_ID = -1
const val TIMER_RESUME_NOTIFICATION_ID = -2
private const val TIMER_COMPLETED_NOTIFICATION_ID = -3