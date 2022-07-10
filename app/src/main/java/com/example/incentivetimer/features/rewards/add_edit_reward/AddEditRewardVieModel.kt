package com.example.incentivetimer.features.rewards.add_edit_reward

import androidx.lifecycle.*
import com.example.incentivetimer.core.ui.IconKey
import com.example.incentivetimer.core.ui.defaultRewardIcon
import com.example.incentivetimer.data.Reward
import com.example.incentivetimer.data.RewardDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRewardVieModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val rewardDao: RewardDao
) : ViewModel(), AddEditRewardScreenActions {
    private var rewardLiveData = savedStateHandle.getLiveData<Reward>(
        KEY_REWARD_LIVE_DATA,
        Reward(defaultRewardIcon, "", 10)
    )
    val rewardInput: LiveData<Reward> = rewardLiveData

    private val rewardId = AddEditRewardScreenSpec.getRewardIdFromSavedStateHandle(savedStateHandle)
    val isEditMode = AddEditRewardScreenSpec.isEditMode(rewardId)

    private val eventChannel = Channel<AddEditRewardEvent>()
    val events: Flow<AddEditRewardEvent> = eventChannel.receiveAsFlow()

    private val showRewardIconSelectionDialogLiveData =
        savedStateHandle.getLiveData("showRewardIconSelectionDialogLiveData", false)
    val showRewardIconSelectionDialog: LiveData<Boolean> = showRewardIconSelectionDialogLiveData

    private val unlockedStateCheckBoxVisibleLiveData =
        savedStateHandle.getLiveData("unlockedStateCheckBoxVisibleLiveData", false)
    val unlockedStateCheckBoxVisible: LiveData<Boolean> = unlockedStateCheckBoxVisibleLiveData


    private val showRewardDeleteConfirmationDialogLiveData =
        savedStateHandle.getLiveData("showRewardDeleteConfirmationDialogLiveData", false)
    val showRewardDeleteConfirmationDialog: LiveData<Boolean> =
        showRewardDeleteConfirmationDialogLiveData

    private val rewardNameInputErrorLiveData =
        savedStateHandle.getLiveData("rewardNameInputErrorLiveData", false)
    val rewardNameInputError: LiveData<Boolean> = rewardNameInputErrorLiveData


    init {
        if (!savedStateHandle.contains(KEY_REWARD_LIVE_DATA)) {
            if (isEditMode && rewardId != null) {
                viewModelScope.launch {
                    rewardLiveData.value = rewardDao.getRewardById(rewardId).firstOrNull()
                }
            } else {
                rewardLiveData.value = Reward(defaultRewardIcon, "", 10)
            }
        }

        if (rewardId != null) {
            viewModelScope.launch {
                rewardDao.getRewardById(rewardId).distinctUntilChangedBy { it?.isUnlocked }
                    .filter { it?.isUnlocked == true }
                    .collectLatest { reward ->
                        if (reward != null) {
                            unlockedStateCheckBoxVisibleLiveData.value = reward.isUnlocked
                            rewardLiveData.value =
                                rewardLiveData.value?.copy(isUnlocked = reward.isUnlocked)

                        }
                    }
            }
        }
    }


    override fun onRewardNameInputChanged(input: String) {
        rewardLiveData.value = rewardLiveData.value?.copy(name = input)
    }

    override fun onChanceInputChanged(input: Int) {
        rewardLiveData.value = rewardLiveData.value?.copy(chanceInPercent = input)

    }

    override fun onIconSelected(iconKey: IconKey) {
        rewardLiveData.value = rewardLiveData.value?.copy(iconKey = iconKey)
    }


    override fun onSaveClicked() {
        val reward = rewardLiveData.value ?: return
        rewardNameInputErrorLiveData.value = false

        viewModelScope.launch {
            if (reward.name.isNotBlank()) {
                if (isEditMode) {
                    updateReward(reward)
                } else {
                    createReward(reward)
                }
            } else {
                rewardNameInputErrorLiveData.value = true
            }
        }
    }

    override fun onRewardUnlockedCheckedChanged(unlocked: Boolean) {
        rewardLiveData.value = rewardLiveData.value?.copy(isUnlocked = unlocked)

    }

    private suspend fun updateReward(reward: Reward) {
        rewardDao.updateReward(reward)
        eventChannel.send(AddEditRewardEvent.RewardUpdated)
    }

    override fun onRewardIconButtonClicked() {
        showRewardIconSelectionDialogLiveData.value = true
    }

    override fun onRewardIconDialogDismissRequest() {
        showRewardIconSelectionDialogLiveData.value = false
    }

    override fun onDeleteRewardClicked() {
        showRewardDeleteConfirmationDialogLiveData.value = true
    }

    override fun onDeleteRewardConfirmed() {
        val reward = rewardLiveData.value ?: return
        viewModelScope.launch {
            rewardDao.deleteReward(reward)
            eventChannel.send(AddEditRewardEvent.RewardDeleted)
        }
    }

    override fun onDeleteRewardDialogDismiss() {
        showRewardDeleteConfirmationDialogLiveData.value = false
    }


    private suspend fun createReward(reward: Reward) {
        rewardDao.insertReward(reward)
        eventChannel.send(AddEditRewardEvent.RewardCreated)
    }
}

sealed class AddEditRewardEvent {
    object RewardCreated : AddEditRewardEvent()
    object RewardUpdated : AddEditRewardEvent()
    object RewardDeleted : AddEditRewardEvent()
}

const val KEY_REWARD_LIVE_DATA = "KEY_REWARD_LIVE_DATA"
const val ADD_EDIT_REWARD_RESULT = "ADD_EDIT_REWARD_RESULT"
const val RESULT_REWARD_ADDED = "RESULT_REWARD_ADDED"
const val RESULT_REWARD_UPDATED = "RESULT_REWARD_UPDATED"
const val RESULT_REWARD_DELETED = "RESULT_REWARD_DELETED"
