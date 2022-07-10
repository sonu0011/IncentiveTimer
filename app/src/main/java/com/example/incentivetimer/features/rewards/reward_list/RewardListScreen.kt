package com.example.incentivetimer.features.rewards.reward_list

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.incentivetimer.R
import com.example.incentivetimer.core.ui.IconKey
import com.example.incentivetimer.core.ui.composables.SimpleConfirmationDialog
import com.example.incentivetimer.core.ui.defaultRewardIcon
import com.example.incentivetimer.core.ui.listBottomPadding
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.data.Reward
import kotlinx.coroutines.launch

@Composable
fun RewardListScreenContent(
    showDeleteAllUnlockedRewardsDialog: Boolean,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    rewardList: List<Reward>,
    actions: RewardListActions,
    onAddNewRewardClicked: () -> Unit,
    onRewardItemClicked: (Long) -> Unit,
) {

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier,
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
                items(rewardList, key = { it.id }) { reward ->
                    RewardItem(
                        reward = reward,
                        onRewardItemClicked = { id ->
                            onRewardItemClicked(id)
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
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

    if (showDeleteAllUnlockedRewardsDialog) {
        SimpleConfirmationDialog(
            title = R.string.delete_all_unlocked_rewards,
            text = R.string.delete_all_unlocked_rewards_confirmation_text,
            dismissAction = actions::onDeleteAllUnlockedRewardsDialogDismissed,
            confirmAction = actions::onDeleteAllUnlockedRewardsConfirmed
        )
    }
}

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(text = stringResource(id = R.string.chance) + ":${reward.chanceInPercent}%")
            }
            if (reward.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.reward_unlocked),
                    modifier = Modifier
                        .size(64.dp)
                        .padding(start = 8.dp),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }

}

@Composable
fun RewardListScreenTopBar(
    actions: RewardListActions,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.reward_list))
        },
        actions = {

            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.open_menu)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(onClick = {
                        expanded = false
                        actions.onDeleteAllUnlockedRewardsClicked()
                    }) {
                        Text(stringResource(R.string.delete_all_unlocked_rewards))
                    }
                }
            }
        }
    )

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
            RewardListScreenContent(
                onAddNewRewardClicked = {},
                onRewardItemClicked = {},
                rewardList = listOf(),
                showDeleteAllUnlockedRewardsDialog = true,
                actions = object : RewardListActions {
                    override fun onDeleteAllUnlockedRewardsClicked() {}
                    override fun onDeleteAllUnlockedRewardsConfirmed() {}
                    override fun onDeleteAllUnlockedRewardsDialogDismissed() {}
                }
            )
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
fun RewardItemPreview() {
    IncentiveTimerTheme {
        Surface() {
            RewardItem(
                Reward(defaultRewardIcon, "Test", 10, false),
                onRewardItemClicked = {}
            )
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
fun RewardItemUnlockedPreview() {
    IncentiveTimerTheme {
        Surface() {
            RewardItem(
                Reward(defaultRewardIcon, "Test", 10, true),
                onRewardItemClicked = {}
            )
        }
    }
}

