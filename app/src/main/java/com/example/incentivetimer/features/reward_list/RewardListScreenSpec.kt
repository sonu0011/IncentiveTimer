package com.example.incentivetimer.features.reward_list

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.incentivetimer.R
import com.example.incentivetimer.core.screenspecs.ScreenSpec

object RewardListScreenSpec : ScreenSpec {
    override val navHostRoute: String = "reward_list"

    @Composable
    override fun TopBar(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        RewardListScreenTopBar()
    }

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        RewardListScreen(navController = navController)
    }
}