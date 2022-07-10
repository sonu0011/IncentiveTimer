package com.example.incentivetimer.features.rewards

import com.example.incentivetimer.core.notification.NotificationHelper
import com.example.incentivetimer.data.RewardDao
import com.example.incentivetimer.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

class RewardUnlockManager @Inject constructor(
    private val rewardDao: RewardDao,
    private val notificationHelper: NotificationHelper,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    fun rollAllRewards() {
        applicationScope.launch {
            val allNotUnlockRewards = rewardDao.getAllUnlockedRewards().first()
            allNotUnlockRewards.forEach { reward ->
                val chanceInPercent = reward.chanceInPercent
                val randomNumber = Random.nextInt(from = 1, until = 100)
                val unlocked = chanceInPercent > randomNumber
                if (unlocked) {
                    val rewardUpdate = reward.copy(isUnlocked = unlocked)
                    rewardDao.updateReward(rewardUpdate)
                    notificationHelper.showNotificationWhenRewardIsUnlocked(reward)
                }

            }
        }
    }
}