package com.example.incentivetimer.features.reward_list

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.incentivetimer.R
import com.example.incentivetimer.application.FullDestinations
import com.example.incentivetimer.core.ui.IconKey
import com.example.incentivetimer.core.ui.listBottomPadding
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.data.Reward
import com.example.incentivetimer.features.add_edit_reward.ARG_REWARD_ID
import kotlinx.coroutines.launch

@Composable
fun RewardListScreen(
    navController: NavController
) {
    ScreenContent(
        onAddNewRewardClicked = {
            navController.navigate(FullDestinations.AddEditRewardScreen.route)
        },
        onRewardItemClicked = { id ->
            navController.navigate(FullDestinations.AddEditRewardScreen.route + "?$ARG_REWARD_ID=$id")
        }
    )
}

@Composable
private fun ScreenContent(
    viewModel: RewardListViewModel = hiltViewModel(),
    onAddNewRewardClicked: () -> Unit,
    onRewardItemClicked: (Long) -> Unit,
) {
    val dummyRewards by viewModel.rewards.observeAsState(listOf())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.reward_list))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp),
                onClick = {
                    onAddNewRewardClicked()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_new_reward)
                )
            }
        }
    ) {
        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = listBottomPadding,
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp
                ),
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(dummyRewards) { reward ->
                    RewardItem(reward = reward, onRewardItemClicked = { id ->
                        onRewardItemClicked(id)
                    })
                }
            }

            AnimatedVisibility(
                visible = listState.firstVisibleItemIndex > 5,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    backgroundColor = Color.LightGray,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = stringResource(id = R.string._scroll_to_top)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RewardItem(
    reward: Reward,
    modifier: Modifier = Modifier,
    onRewardItemClicked: (Long) -> Unit,


    ) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = { onRewardItemClicked(reward.id) },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .size(64.dp),
                imageVector = IconKey.valueOf(reward.iconKey.name).rewardIcon,
                contentDescription = null
            )
            Column() {
                Text(
                    text = reward.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6
                )
                Text(text = stringResource(id = R.string.chance) + ":${reward.chanceInPercent}%")
            }
        }
    }

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
            ScreenContent(onAddNewRewardClicked = {}, onRewardItemClicked = {})
        }
    }
}

