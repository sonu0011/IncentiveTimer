package com.example.incentivetimer.application

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.incentivetimer.R
import com.example.incentivetimer.features.add_edit_reward.AddEditRewardScreen
import com.example.incentivetimer.features.reward_list.RewardListScreen
import com.example.incentivetimer.features.timer.TimerScreen
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.features.add_edit_reward.ARG_REWARD_ID
import com.example.incentivetimer.features.add_edit_reward.NO_REWARD_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IncentiveTimerTheme {
                ScreenContent()
            }
        }
    }
}

@Composable
private fun ScreenContent() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val hideBottomBar = navBackStackEntry?.arguments?.getBoolean(ARG_HIDE_BOTTOM_BAR)
            val currentDestination = navBackStackEntry?.destination
            if (hideBottomBar == null || !hideBottomBar) {
                BottomNavigation {
                    bottomNavDestinations.forEach { bottomNavDestination ->
                        BottomNavigationItem(
                            selected = currentDestination?.hierarchy?.any { it.route == bottomNavDestination.route } == true,
                            onClick = {
                                navController.navigate(bottomNavDestination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(text = stringResource(id = bottomNavDestination.label)) },
                            icon = {
                                Icon(
                                    bottomNavDestination.icon,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = bottomNavDestinations[0].route
        ) {
            composable(BottomNavDestination.Timer.route) {
                TimerScreen(navController)
            }
            composable(BottomNavDestination.RewardList.route) {
                RewardListScreen(navController)
            }
            composable(
                route = FullDestinations.AddEditRewardScreen.route + "?$ARG_REWARD_ID={$ARG_REWARD_ID}",
                arguments = listOf(
                    navArgument(ARG_REWARD_ID) {
                        type = NavType.LongType
                        defaultValue = NO_REWARD_ID
                    },
                    navArgument(ARG_HIDE_BOTTOM_BAR) {
                        defaultValue = true
                    }
                )
            ) {
                AddEditRewardScreen(navController)
            }
        }
    }
}

val bottomNavDestinations = listOf(
    BottomNavDestination.Timer,
    BottomNavDestination.RewardList
)

sealed class BottomNavDestination(
    val route: String,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    object Timer : BottomNavDestination("timer", Icons.Default.Timer, R.string.timer)
    object RewardList :
        BottomNavDestination("reward_list", Icons.Default.List, R.string.reward_list)
}

sealed class FullDestinations(val route: String) {
    object AddEditRewardScreen : FullDestinations("add_edit_screen")
}

@Preview(
    name = "Light Mode",
    uiMode = UI_MODE_NIGHT_NO
)

@Preview(
    showBackground = true,
    name = "Dark Mode",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun DefaultPreview() {
    IncentiveTimerTheme {
        Surface() {
            ScreenContent()
        }
    }
}

const val ARG_HIDE_BOTTOM_BAR = "ARG_HIDE_BOTTOM_BAR"