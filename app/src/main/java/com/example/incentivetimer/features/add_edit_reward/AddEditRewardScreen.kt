package com.example.incentivetimer.features.add_edit_reward

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.incentivetimer.R
import com.example.incentivetimer.core.composables.ITIconButton
import com.example.incentivetimer.core.ui.IconKey
import com.example.incentivetimer.core.ui.defaultRewardIcon
import com.example.incentivetimer.core.util.exhaustive
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment


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
}

@Composable
fun AddEditRewardScreen(
    navController: NavController,
    viewModel: AddEditRewardVieModel = hiltViewModel()
) {
    val isEditMode = viewModel.isEditMode
    val rewardNameInput by viewModel.rewardNameInput.observeAsState("")
    val chanceInput by viewModel.chanceInput.observeAsState(10)
    val shouldShowRewardIconSelectedDialog by
    viewModel.showRewardIconSelectionDialog.observeAsState(false)

    val shouldShowRewardDeleteConfirmationDialog by
    viewModel.showRewardDeleteConfirmationDialog.observeAsState(false)

    val rewardIconSelection by viewModel.rewardIconKey.observeAsState(defaultRewardIcon)
    val rewardNameInputError by viewModel.rewardNameInputError.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddEditRewardEvent.RewardCreated -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        ADD_EDIT_REWARD_RESULT, RESULT_REWARD_ADDED
                    )
                    navController.popBackStack()
                }
                AddEditRewardEvent.RewardUpdated -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        ADD_EDIT_REWARD_RESULT, RESULT_REWARD_UPDATED
                    )
                    navController.popBackStack()
                }
                AddEditRewardEvent.RewardDeleted -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        ADD_EDIT_REWARD_RESULT, RESULT_REWARD_DELETED
                    )
                    navController.popBackStack()
                }
            }.exhaustive

        }
    }

    ScreenContent(
        isEditMode = isEditMode,
        rewardNameInput = rewardNameInput,
        chanceInput = chanceInput,
        rewardIconSelection = rewardIconSelection,
        actions = viewModel,
        shouldShowRewardIconSelectedDialog = shouldShowRewardIconSelectedDialog,
        hasRewardNameInputError = rewardNameInputError,
        shouldShowRewardDeleteConfirmationDialog = shouldShowRewardDeleteConfirmationDialog,
        onCloseClicked = { navController.popBackStack() },
    )

}

@Composable
private fun ScreenContent(
    isEditMode: Boolean,
    rewardNameInput: String,
    chanceInput: Int,
    rewardIconSelection: IconKey,
    onCloseClicked: () -> Unit,
    actions: AddEditRewardScreenActions,
    hasRewardNameInputError: Boolean,
    shouldShowRewardDeleteConfirmationDialog: Boolean,
    shouldShowRewardIconSelectedDialog: Boolean,
) {
    Scaffold(
        topBar = {
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
        },
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
                value = rewardNameInput,
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
            Text(text = stringResource(id = R.string.chance) + ": $chanceInput%")
            Slider(
                value = chanceInput.toFloat().div(100),
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
                    imageVector = rewardIconSelection.rewardIcon,
                    contentDescription = stringResource(id = R.string.select_icon),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
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
        AlertDialog(
            onDismissRequest = actions::onDeleteRewardDialogDismiss,
            title = {
                Text(text = stringResource(id = R.string.confirm_deletion))
            },
            text = {
                Text(text = stringResource(id = R.string.confirm_reward_deletion_text))
            },
            confirmButton = {
                TextButton(onClick = actions::onDeleteRewardConfirmed) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = actions::onDeleteRewardDialogDismiss) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        )
    }
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


//@Preview(
//    showBackground = false,
//    name = "Light Mode",
//    uiMode = Configuration.UI_MODE_NIGHT_NO
//)
//
//@Preview(
//    showBackground = true,
//    name = "Dark Mode",
//    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//fun DefaultPreview() {
//    IncentiveTimerTheme {
//        Surface() {
//            ScreenContent(
//                isEditMode = true,
//                rewardNameInput = "Cake",
//                onRewardNameInputChanged = {},
//                onChanceInputChanged = {},
//                chanceInput = 10,
//                onCloseClicked = {},
//                onSaveClicked = {},
//                onRewardIconButtonClicked = {}
//            )
//        }
//    }
//}