package com.example.incentivetimer.features.add_edit_reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.incentivetimer.core.ui.IconKey
import com.example.incentivetimer.core.ui.defaultRewardIcon
import com.example.incentivetimer.data.Reward
import com.example.incentivetimer.data.RewardDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRewardVieModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val rewardDao: RewardDao
) : ViewModel(), AddEditRewardScreenActions {

    private var reward: Reward? = null

    private val rewardId = savedStateHandle.get<Long>(ARG_REWARD_ID)
    val isEditMode = rewardId != NO_REWARD_ID

    private val eventChannel = Channel<AddEditRewardEvent>()
    val events: Flow<AddEditRewardEvent> = eventChannel.receiveAsFlow()


    private val rewardNameLiveData = savedStateHandle.getLiveData<String>("rewardNameLiveData")
    val rewardNameInput: LiveData<String> = rewardNameLiveData

    private val chanceLiveData =
        savedStateHandle.getLiveData<Int>("chanceLiveData")
    val chanceInput: LiveData<Int> = chanceLiveData

    private val rewardIconKeyLiveData =
        savedStateHandle.getLiveData<IconKey>("rewardIconKeyLiveData")
    val rewardIconKey: LiveData<IconKey> = rewardIconKeyLiveData


    private val showRewardIconSelectionDialogLiveData =
        savedStateHandle.getLiveData("showRewardIconSelectionDialogLiveData", false)
    val showRewardIconSelectionDialog: LiveData<Boolean> = showRewardIconSelectionDialogLiveData


    private val rewardNameInputErrorLiveData =
        savedStateHandle.getLiveData("rewardNameInputErrorLiveData", false)
    val rewardNameInputError: LiveData<Boolean> = rewardNameInputErrorLiveData


    init {
        if (rewardId != null && rewardId != NO_REWARD_ID) {
            viewModelScope.launch {
                reward = rewardDao.getRewardById(rewardId = rewardId)
                populateEmptyInputValuesWithRewardData()
            }
        } else {
            populateInputValuesWithDefaultValues()
        }
    }

    private fun populateInputValuesWithDefaultValues() {
        rewardNameLiveData.value = ""
        chanceLiveData.value = 10
        rewardIconKeyLiveData.value = IconKey.STAR
    }

    private fun populateEmptyInputValuesWithRewardData() {
        val reward = reward
        if (reward != null) {
            if (rewardNameLiveData.value == null)
                rewardNameLiveData.value = reward.title
            if (chanceLiveData.value == null)
                chanceLiveData.value = reward.chanceInPercent
            if (rewardIconKeyLiveData.value == null)
                rewardIconKeyLiveData.value = reward.iconKey
        }
    }

    override fun onRewardNameInputChanged(input: String) {
        rewardNameLiveData.value = input
    }

    override fun onChanceInputChanged(input: Int) {
        chanceLiveData.value = input
    }

    override fun onSaveClicked() {
        val rewardInput = rewardNameInput.value
        val chanceInput = chanceInput.value
        val rewardIconSelection = rewardIconKey.value
        rewardNameInputErrorLiveData.value = false

        viewModelScope.launch {
            if (!rewardInput.isNullOrBlank() && chanceInput != null && rewardIconSelection != null) {
                val reward = reward
                if (reward != null) {
                    updateReward(
                        reward.copy(
                            iconKey = rewardIconSelection,
                            title = rewardInput,
                            chanceInPercent = chanceInput
                        )
                    )
                } else {
                    createReward(
                        Reward(
                            iconKey = rewardIconSelection!!,
                            title = rewardInput,
                            chanceInPercent = chanceInput
                        )
                    )
                }
            } else {
                if (rewardInput.isNullOrBlank()) {
                    rewardNameInputErrorLiveData.value = true
                }
            }
        }
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

    override fun onIconSelected(iconKey: IconKey) {
        rewardIconKeyLiveData.value = iconKey
    }

    private suspend fun createReward(reward: Reward) {
        rewardDao.insertReward(reward)
        eventChannel.send(AddEditRewardEvent.RewardCreated)
    }
}

sealed class AddEditRewardEvent {
    object RewardCreated : AddEditRewardEvent()
    object RewardUpdated : AddEditRewardEvent()
}

const val ARG_REWARD_ID = "rewardId"
const val NO_REWARD_ID = -1L