package com.example.incentivetimer.features.rewards.reward_list

import androidx.lifecycle.*
import com.example.incentivetimer.data.Reward
import com.example.incentivetimer.data.RewardDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardListViewModel @Inject constructor(
    private val rewardDao: RewardDao,
    savedStateHandle: SavedStateHandle,

    ) : ViewModel(), RewardListActions {
    val rewards = rewardDao.getAllRewardsSortedByIsUnlocked().asLiveData()

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val showDeleteAllUnlockedRewardsDialogLiveData =
        savedStateHandle.getLiveData<Boolean>("showDeleteAllUnlockedRewardsDialogLiveData", false)
    val showDeleteAllUnlockedRewardsDialog: LiveData<Boolean> =
        showDeleteAllUnlockedRewardsDialogLiveData

    override fun onDeleteAllUnlockedRewardsClicked() {
        showDeleteAllUnlockedRewardsDialogLiveData.value = true
    }

    override fun onDeleteAllUnlockedRewardsConfirmed() {
        showDeleteAllUnlockedRewardsDialogLiveData.value = false
        viewModelScope.launch {
            rewardDao.deleteAllUnlockedRewards()
        }
    }

    override fun onDeleteAllUnlockedRewardsDialogDismissed() {
        showDeleteAllUnlockedRewardsDialogLiveData.value = false
    }

    override fun onRewardSwiped(reward: Reward) {
        viewModelScope.launch {
            rewardDao.deleteReward(reward)
            eventChannel.send(Event.ShowUndoRewardSnackBar(reward))
        }
    }

    override fun onUndoDeleteRewardConfirmed(reward: Reward) {
        viewModelScope.launch {
            rewardDao.insertReward(reward)
        }
    }

    sealed class Event {
        data class ShowUndoRewardSnackBar(val reward: Reward) : Event()
    }
}
