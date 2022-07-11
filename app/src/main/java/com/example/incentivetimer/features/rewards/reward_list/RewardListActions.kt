package com.example.incentivetimer.features.rewards.reward_list

import com.example.incentivetimer.data.Reward

interface RewardListActions {
    fun onDeleteAllUnlockedRewardsClicked()
    fun onDeleteAllUnlockedRewardsConfirmed()
    fun onDeleteAllUnlockedRewardsDialogDismissed()
    fun onRewardSwiped(reward: Reward)
    fun onUndoDeleteRewardConfirmed(reward: Reward)
}