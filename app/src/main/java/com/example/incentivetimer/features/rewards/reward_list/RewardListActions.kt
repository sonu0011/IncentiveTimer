package com.example.incentivetimer.features.rewards.reward_list

import com.example.incentivetimer.data.Reward

interface RewardListActions {
    fun onDeleteAllUnlockedRewardsClicked()
    fun onDeleteAllUnlockedRewardsConfirmed()
    fun onDeleteAllUnlockedRewardsDialogDismissed()
    fun onDeleteAllSelectedRewardsConfirmed()
    fun onDeleteAllSelectedRewardsDialogDismissed()
    fun onRewardSwiped(reward: Reward)
    fun onRewardClicked(reward: Reward)
    fun onRewardLongClicked(reward: Reward)
    fun onUndoDeleteRewardConfirmed(reward: Reward)
    fun onCancelMultiSelectionModeClicked()
    fun onDeleteAllSelectedItemsClicked()
}