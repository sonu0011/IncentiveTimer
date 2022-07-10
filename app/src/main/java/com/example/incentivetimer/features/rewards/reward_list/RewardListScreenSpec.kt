package com.example.incentivetimer.features.rewards.reward_list

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.incentivetimer.R
import com.example.incentivetimer.core.screenspecs.ScreenSpec
import com.example.incentivetimer.features.rewards.add_edit_reward.*

object RewardListScreenSpec : ScreenSpec {
    override val navHostRoute: String = "reward_list"

    @Composable
    override fun TopBar(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        RewardListScreenTopBar()
    }

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: RewardListViewModel = hiltViewModel(navBackStackEntry)

        val rewards by viewModel.rewards.observeAsState(listOf())
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

        RewardListScreenContent(
            scaffoldState = scaffoldState,
            onAddNewRewardClicked = {
                navController.navigate(AddEditRewardScreenSpec.buildRoute())
            },
            onRewardItemClicked = { id ->
                navController.navigate(AddEditRewardScreenSpec.buildRoute(id))
            },
            rewardList = rewards
        )
    }
}