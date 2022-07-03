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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.incentivetimer.R
import com.example.incentivetimer.core.screenspecs.ScreenSpec
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.features.add_edit_reward.AddEditRewardScreenSpec
import com.example.incentivetimer.features.reward_list.RewardListScreenSpec
import com.example.incentivetimer.features.timer.TimeScreenSpec
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val hideBottomBar = navBackStackEntry?.arguments?.getBoolean(ARG_HIDE_BOTTOM_BAR)
    val currentDestination = navBackStackEntry?.destination

    val screenSpec = ScreenSpec.allScreens[currentDestination?.route]


    Scaffold(
        topBar = {
            val navBackStackEntry = navBackStackEntry
            if (navBackStackEntry != null) {
                screenSpec?.TopBar(
                    navController = navController,
                    navBackStackEntry = navBackStackEntry
                )
            }
        },
        bottomBar = {
            if (hideBottomBar == null || !hideBottomBar) {
                BottomNavigation {
                    bottomNavDestinations.forEach { bottomNavDestination ->
                        BottomNavigationItem(
                            selected = currentDestination?.hierarchy?.any { it.route == bottomNavDestination.screenSpec.navHostRoute } == true,
                            onClick = {
                                navController.navigate(bottomNavDestination.screenSpec.navHostRoute) {
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
            startDestination = bottomNavDestinations[0].screenSpec.navHostRoute
        ) {
            ScreenSpec.allScreens.values.forEach { screenSpec ->
                composable(
                    route = screenSpec.navHostRoute,
                    arguments = screenSpec.arguments,
                    deepLinks = screenSpec.deepLinks
                ) { navBackStackEntry ->
                    screenSpec.Content(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry
                    )
                }
            }
        }
    }
}

val bottomNavDestinations = listOf(
    BottomNavDestination.Timer,
    BottomNavDestination.RewardList
)

sealed class BottomNavDestination(
    val screenSpec: ScreenSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    object Timer :
        BottomNavDestination(
            screenSpec = TimeScreenSpec,
            Icons.Default.Timer,
            R.string.timer
        )

    object RewardList :
        BottomNavDestination(
            screenSpec = RewardListScreenSpec,
            Icons.Default.Star,
            R.string.rewards
        )
}

sealed class FullDestinations(
    val screenSpec: ScreenSpec
) {
    object AddEditRewardScreen : FullDestinations(screenSpec = AddEditRewardScreenSpec)
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