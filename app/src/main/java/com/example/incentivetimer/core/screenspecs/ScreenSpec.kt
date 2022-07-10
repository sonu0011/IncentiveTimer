package com.example.incentivetimer.core.screenspecs

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import com.example.incentivetimer.features.rewards.add_edit_reward.AddEditRewardScreenSpec
import com.example.incentivetimer.features.rewards.reward_list.RewardListScreenSpec
import com.example.incentivetimer.features.timer.TimeScreenSpec

interface ScreenSpec {
    companion object {
        val allScreens = listOf(
            TimeScreenSpec,
            RewardListScreenSpec,
            AddEditRewardScreenSpec
        ).associateBy { it.navHostRoute }
    }

    val navHostRoute: String

    @Composable
    fun TopBar(navController: NavController,navBackStackEntry: NavBackStackEntry)

    val arguments: List<NamedNavArgument> get() = emptyList()

    val deepLinks: List<NavDeepLink> get() = emptyList()

    @Composable
    fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry
    )
}