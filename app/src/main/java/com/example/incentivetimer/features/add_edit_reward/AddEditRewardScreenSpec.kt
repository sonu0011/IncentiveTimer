package com.example.incentivetimer.features.add_edit_reward

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.example.incentivetimer.application.ARG_HIDE_BOTTOM_BAR
import com.example.incentivetimer.core.screenspecs.ScreenSpec

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
        AddEditRewardScreen(navController = navController)
    }
}

private const val ARG_REWARD_ID = "rewardId"
const val NO_REWARD_ID = -1L