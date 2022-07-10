package com.example.incentivetimer.features.rewards.add_edit_reward

import com.example.incentivetimer.core.ui.IconKey

interface AddEditRewardScreenActions {
    fun onRewardNameInputChanged(input: String)
    fun onChanceInputChanged(input: Int)
    fun onSaveClicked()
    fun onRewardIconButtonClicked()
    fun onRewardIconDialogDismissRequest()
    fun onIconSelected(iconKey: IconKey)
    fun onDeleteRewardClicked()
    fun onDeleteRewardConfirmed()
    fun onDeleteRewardDialogDismiss()
    fun onRewardUnlockedCheckedChanged(unlocked: Boolean)
}