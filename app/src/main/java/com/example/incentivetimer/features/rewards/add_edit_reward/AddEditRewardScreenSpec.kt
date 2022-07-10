package com.example.incentivetimer.features.rewards.add_edit_reward

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.example.incentivetimer.application.ARG_HIDE_BOTTOM_BAR
import com.example.incentivetimer.core.screenspecs.ScreenSpec
import com.example.incentivetimer.core.ui.defaultRewardIcon
import com.example.incentivetimer.core.util.exhaustive
import com.example.incentivetimer.data.Reward

object AddEditRewardScreenSpec : ScreenSpec {
    override val navHostRoute: String = "add_edit_screen?$ARG_REWARD_ID={$ARG_REWARD_ID}"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(ARG_REWARD_ID) {
                type = NavType.LongType
                defaultValue = NO_REWARD_ID
            },
            navArgument(ARG_HIDE_BOTTOM_BAR) {
                defaultValue = true
            }
        )

    fun isEditMode(rewardId: Long?) = rewardId != NO_REWARD_ID

    fun buildRoute(rewardId: Long = NO_REWARD_ID): String =
        "add_edit_screen?$ARG_REWARD_ID=$rewardId"

    fun getRewardIdFromSavedStateHandle(savedStateHandle: SavedStateHandle) =
        savedStateHandle.get<Long>(ARG_REWARD_ID)

    @Composable
    override fun TopBar(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: AddEditRewardVieModel = hiltViewModel(navBackStackEntry)
        val rewardId = navBackStackEntry.arguments?.getLong(ARG_REWARD_ID)
        AddEditRewardScreenTopBar(
            isEditMode = isEditMode(rewardId),
            actions = viewModel,
            onCloseClicked = {
                navController.popBackStack()
            }
        )
    }

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: AddEditRewardVieModel = hiltViewModel(navBackStackEntry)
        val isEditMode = viewModel.isEditMode
        val rewardInput by viewModel.rewardInput.observeAsState(
            viewModel.rewardInput.value!!
        )
        val shouldShowRewardIconSelectedDialog by
        viewModel.showRewardIconSelectionDialog.observeAsState(false)

        val unlockedStateCheckBoxVisible by
        viewModel.unlockedStateCheckBoxVisible.observeAsState(false)

        val shouldShowRewardDeleteConfirmationDialog by
        viewModel.showRewardDeleteConfirmationDialog.observeAsState(false)
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

        AddEditRewardScreenContent(
            isEditMode = isEditMode,
            rewardInput = rewardInput,
            actions = viewModel,
            unlockedStateCheckBoxVisible = unlockedStateCheckBoxVisible,
            shouldShowRewardIconSelectedDialog = shouldShowRewardIconSelectedDialog,
            hasRewardNameInputError = rewardNameInputError,
            shouldShowRewardDeleteConfirmationDialog = shouldShowRewardDeleteConfirmationDialog,
        )
    }
}

private const val ARG_REWARD_ID = "rewardId"
const val NO_REWARD_ID = -1L