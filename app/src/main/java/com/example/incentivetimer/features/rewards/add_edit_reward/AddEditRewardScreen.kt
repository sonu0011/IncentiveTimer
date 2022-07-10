package com.example.incentivetimer.features.rewards.add_edit_reward

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.incentivetimer.R
import com.example.incentivetimer.core.ui.IconKey
import com.example.incentivetimer.core.ui.composables.ITIconButton
import com.example.incentivetimer.core.ui.composables.LabeledCheckbox
import com.example.incentivetimer.core.ui.composables.SimpleConfirmationDialog
import com.example.incentivetimer.core.ui.defaultRewardIcon
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.data.Reward
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment

@Composable
fun AddEditRewardScreenContent(
    isEditMode: Boolean,
    rewardInput: Reward,
    actions: AddEditRewardScreenActions,
    unlockedStateCheckBoxVisible: Boolean,
    hasRewardNameInputError: Boolean,
    shouldShowRewardDeleteConfirmationDialog: Boolean,
    shouldShowRewardIconSelectedDialog: Boolean,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = actions::onSaveClicked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.add_new_reward)
                )
            }
        },

        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val focusRequester by remember { mutableStateOf(FocusRequester()) }

            TextField(
                value = rewardInput.name,
                onValueChange = { input ->
                    actions.onRewardNameInputChanged(input)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text(text = stringResource(id = R.string.reward_name)) },
                singleLine = true,
                isError = hasRewardNameInputError
            )
            if (!isEditMode) {
                LaunchedEffect(key1 = true) {
                    focusRequester.requestFocus()
                }
            }
            if (hasRewardNameInputError) {
                Text(
                    text = stringResource(id = R.string.field_cant_be_empty),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = R.string.chance) + ": ${rewardInput.chanceInPercent}%")
            Slider(
                value = rewardInput.chanceInPercent.toFloat().div(100),
                onValueChange = { chanceAsFloat ->
                    actions.onChanceInputChanged(chanceAsFloat.times(100).toInt())
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ITIconButton(
                onclick = { actions.onRewardIconButtonClicked() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = rewardInput.iconKey.rewardIcon,
                    contentDescription = stringResource(id = R.string.select_icon),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
            if (unlockedStateCheckBoxVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                LabeledCheckbox(
                    text = stringResource(id = R.string.unlocked),
                    checked = rewardInput.isUnlocked,
                    onCheckedChange = actions::onRewardUnlockedCheckedChanged
                )
            }
        }
    }
    if (shouldShowRewardIconSelectedDialog) {
        RewardIconSelectionDialog(
            onDismissRequest = actions::onRewardIconDialogDismissRequest,
            onIconSelected = actions::onIconSelected,
        )
    }

    if (shouldShowRewardDeleteConfirmationDialog) {
        SimpleConfirmationDialog(
            title = R.string.confirm_deletion,
            text = R.string.confirm_reward_deletion_text,
            dismissAction = actions::onDeleteRewardDialogDismiss,
            confirmAction = actions::onDeleteRewardConfirmed
        )
    }
}

@Composable
fun AddEditRewardScreenTopBar(
    isEditMode: Boolean,
    actions: AddEditRewardScreenActions,
    onCloseClicked: () -> Unit,
) {
    val appTitle = if (isEditMode) stringResource(id = R.string.edit_reward)
    else stringResource(id = R.string.add_reward)
    TopAppBar(
        title = {
            Text(text = appTitle)
        },
        navigationIcon = {
            IconButton(onClick = { onCloseClicked() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.close)
                )
            }
        },
        actions = {
            if (isEditMode) {
                var expended by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { expended = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.open_menu)
                        )
                    }
                    DropdownMenu(
                        expanded = expended,
                        onDismissRequest = { expended = false }) {
                        DropdownMenuItem(
                            onClick = {
                                expended = false
                                actions.onDeleteRewardClicked()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.delete_reward))
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun RewardIconSelectionDialog(
    onDismissRequest: () -> Unit,
    onIconSelected: (iconKey: IconKey) -> Unit,
) {

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        text = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = MainAxisAlignment.Center
            ) {
                IconKey.values().forEach { iconKey ->
                    IconButton(
                        onClick = {
                            onIconSelected(iconKey)
                            onDismissRequest()
                        }
                    ) {
                        Icon(
                            imageVector = iconKey.rewardIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp)
                        )
                    }
                }
            }
        },
        buttons = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.cancel))

            }
        }
    )

}


@Preview(
    showBackground = false,
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)

@Preview(
    showBackground = true,
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DefaultPreview() {
    IncentiveTimerTheme {
        Surface() {
            AddEditRewardScreenContent(
                isEditMode = true,
                hasRewardNameInputError = false,
                shouldShowRewardIconSelectedDialog = true,
                shouldShowRewardDeleteConfirmationDialog = true,
                unlockedStateCheckBoxVisible = false,
                rewardInput = Reward(
                    defaultRewardIcon, "", 10
                ),
                actions = object : AddEditRewardScreenActions {
                    override fun onRewardNameInputChanged(input: String) {}
                    override fun onChanceInputChanged(input: Int) {}
                    override fun onSaveClicked() {}
                    override fun onRewardIconButtonClicked() {}
                    override fun onRewardIconDialogDismissRequest() {}
                    override fun onIconSelected(iconKey: IconKey) {}
                    override fun onDeleteRewardClicked() {}
                    override fun onDeleteRewardConfirmed() {}
                    override fun onDeleteRewardDialogDismiss() {}
                    override fun onRewardUnlockedCheckedChanged(unlocked: Boolean) {}
                }
            )
        }
    }
}