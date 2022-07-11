package com.example.incentivetimer.features.rewards.reward_list

import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import com.example.incentivetimer.R
import com.example.incentivetimer.core.screenspecs.ScreenSpec
import com.example.incentivetimer.core.util.exhaustive
import com.example.incentivetimer.features.rewards.add_edit_reward.*
import kotlinx.coroutines.flow.collect

object RewardListScreenSpec : ScreenSpec {
    override val navHostRoute: String = "reward_list"

    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink {
            uriPattern = "https://www.incentivetimer.com/reward_list"
        }
    )

    @Composable
    override fun TopBar(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: RewardListViewModel = hiltViewModel(navBackStackEntry)
        RewardListScreenTopBar(actions = viewModel)
    }

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: RewardListViewModel = hiltViewModel(navBackStackEntry)
        val rewards by viewModel.rewards.observeAsState(listOf())
        val showDeleteAllUnlockedRewardsDialog by viewModel.showDeleteAllUnlockedRewardsDialog.observeAsState(
            false
        )
        val scaffoldState = rememberScaffoldState()
        val context = LocalContext.current

        val addEditRewardResult = navController.currentBackStackEntry
            ?.savedStateHandle?.getLiveData<String>(ADD_EDIT_REWARD_RESULT)
            ?.observeAsState()

        LaunchedEffect(key1 = addEditRewardResult) {
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>(
                ADD_EDIT_REWARD_RESULT
            )

            addEditRewardResult?.value?.let { addEditRewardResult ->
                when (addEditRewardResult) {
                    RESULT_REWARD_ADDED -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            context.getString(R.string.reward_added)
                        )
                    }
                    RESULT_REWARD_UPDATED -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            context.getString(R.string.reward_updated)
                        )
                    }
                    RESULT_REWARD_DELETED -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            context.getString(R.string.reward_deleted)
                        )
                    }
                }

            }
        }
        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is RewardListViewModel.Event.ShowUndoRewardSnackBar -> {
                        val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                            message = context.getString(R.string.reward_deleted),
                            actionLabel = context.getString(R.string.undo)
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            viewModel.onUndoDeleteRewardConfirmed(event.reward)
                        }
                        Unit
                    }
                }.exhaustive
            }
        }

        RewardListScreenContent(
            scaffoldState = scaffoldState,
            onAddNewRewardClicked = {
                navController.navigate(AddEditRewardScreenSpec.buildRoute())
            },
            onRewardItemClicked = { id ->
                navController.navigate(AddEditRewardScreenSpec.buildRoute(id))
            },
            rewardList = rewards,
            actions = viewModel,
            showDeleteAllUnlockedRewardsDialog = showDeleteAllUnlockedRewardsDialog
        )
    }
}