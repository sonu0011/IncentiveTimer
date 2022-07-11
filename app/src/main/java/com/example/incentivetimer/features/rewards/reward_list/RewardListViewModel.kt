package com.example.incentivetimer.features.rewards.reward_list

import androidx.lifecycle.*
import com.example.incentivetimer.data.Reward
import com.example.incentivetimer.data.RewardDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardListViewModel @Inject constructor(
    private val rewardDao: RewardDao,
    savedStateHandle: SavedStateHandle,

    ) : ViewModel(), RewardListActions {

    private val rewardsFlow = rewardDao.getAllRewardsSortedByIsUnlocked()
    val rewards: LiveData<List<Reward>> = rewardsFlow.asLiveData()

    private val selectedRewardListLiveData =
        savedStateHandle.getLiveData<List<Reward>>("selectedRewardListLiveData", listOf())
    val selectedRewards: LiveData<List<Reward>> = combine(
        selectedRewardListLiveData.asFlow(),
        rewardsFlow
    ) { selectedRewards, rewards ->
        selectedRewards.filter { rewards.contains(it) }
    }.onEach { selectedRewards ->
        if (selectedRewards.isEmpty()) {
            cancelMultiSelectionMode()
        }
    }.asLiveData()

    val selectedItemCount = selectedRewards.map { it.size }

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()


    private val multipleSelectionModeActiveLiveData =
        savedStateHandle.getLiveData("multipleSelectionModeActiveLiveData", false)
    val multipleSelectionModeActive: LiveData<Boolean> = multipleSelectionModeActiveLiveData


    private val showDeleteAllUnlockedRewardsDialogLiveData =
        savedStateHandle.getLiveData<Boolean>("showDeleteAllUnlockedRewardsDialogLiveData", false)
    val showDeleteAllUnlockedRewardsDialog: LiveData<Boolean> =
        showDeleteAllUnlockedRewardsDialogLiveData


    private val showDeleteAllSelectedRewardsDialogLiveData =
        savedStateHandle.getLiveData("showDeleteAllSelectedRewardsDialogLiveData", false)
    val showDeleteAllSelectedRewardsDialog: LiveData<Boolean> =
        showDeleteAllSelectedRewardsDialogLiveData


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

    override fun onRewardClicked(reward: Reward) {
        val multiSelectionMode = multipleSelectionModeActiveLiveData.value
        if (multiSelectionMode == false) {
            viewModelScope.launch {
                eventChannel.send(Event.NavigateToAddEditRewardScreen(reward))
            }
        } else {
            addOrRemoveSelectedReward(reward)
        }
    }

    override fun onRewardLongClicked(reward: Reward) {
        val multiSelectionMode = multipleSelectionModeActiveLiveData.value
        if (multiSelectionMode == false) {
            multipleSelectionModeActiveLiveData.value = true
        }
        addOrRemoveSelectedReward(reward)
    }

    private fun addOrRemoveSelectedReward(reward: Reward) {
        val selectedRewards = selectedRewardListLiveData.value
        if (selectedRewards != null) {
            if (selectedRewards.contains(reward)) {
                val selectedRewardUpdate = selectedRewards.toMutableList().apply {
                    remove(reward)
                    if (this.isEmpty()) {
                        multipleSelectionModeActiveLiveData.value = false
                    }
                }
                selectedRewardListLiveData.value = selectedRewardUpdate
            } else {
                val selectedRewardUpdate = selectedRewards.toMutableList().apply {
                    add(reward)
                }
                selectedRewardListLiveData.value = selectedRewardUpdate

            }
        }
    }

    override fun onDeleteAllSelectedItemsClicked() {
        showDeleteAllSelectedRewardsDialogLiveData.value = true
    }

    override fun onDeleteAllSelectedRewardsConfirmed() {
        showDeleteAllSelectedRewardsDialogLiveData.value = false
        viewModelScope.launch {
            val rewards = selectedRewards.value ?: emptyList()
            rewardDao.deleteRewards(rewards)
            cancelMultiSelectionMode()
        }
    }

    override fun onDeleteAllSelectedRewardsDialogDismissed() {
        showDeleteAllSelectedRewardsDialogLiveData.value = false
    }

    override fun onCancelMultiSelectionModeClicked() {
        cancelMultiSelectionMode()
    }

    private fun cancelMultiSelectionMode() {
        if (multipleSelectionModeActiveLiveData.value == false) return
        selectedRewardListLiveData.value = emptyList()
        multipleSelectionModeActiveLiveData.value = false
    }


    sealed class Event {
        data class ShowUndoRewardSnackBar(val reward: Reward) : Event()
        data class NavigateToAddEditRewardScreen(val reward: Reward) : Event()
    }
}
