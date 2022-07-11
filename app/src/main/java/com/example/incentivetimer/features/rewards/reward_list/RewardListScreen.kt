package com.example.incentivetimer.features.rewards.reward_list

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.incentivetimer.core.ui.theme.ITtBlue
import com.example.incentivetimer.core.ui.theme.IncentiveTimerTheme
import com.example.incentivetimer.core.ui.theme.PrimaryLightAlpha
import com.example.incentivetimer.data.Reward
import kotlinx.coroutines.launch

@Composable
fun RewardListScreenContent(
    showDeleteAllUnlockedRewardsDialog: Boolean,
    showDeleteAllSelectedRewardsDialog: Boolean,
    selectedRewards: List<Reward>,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    rewardList: List<Reward>,
    actions: RewardListActions,
    onAddNewRewardClicked: () -> Unit,
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
                    val selected = selectedRewards.contains(reward)
                    if (reward.isUnlocked) {
                        val dismissState =
                            rememberDismissState(confirmStateChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToStart || dismissValue == DismissValue.DismissedToEnd) {
                                    actions.onRewardSwiped(reward)
                                }
                                true
                            })

                        SwipeToDismiss(
                            state = dismissState,
                            background = {},
                            modifier = Modifier.animateItemPlacement()
                        ) {
                            RewardItem(
                                reward = reward,
                                selected = selected,
                                onRewardClicked = actions::onRewardClicked,
                                onItemLongClicked = actions::onRewardLongClicked
                            )
                        }
                    } else {
                        RewardItem(
                            reward = reward,
                            selected = selected,
                            onRewardClicked = actions::onRewardClicked,
                            modifier = Modifier.animateItemPlacement(),
                            onItemLongClicked = actions::onRewardLongClicked

                        )
                    }

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
    if (showDeleteAllSelectedRewardsDialog) {
        SimpleConfirmationDialog(
            title = R.string.delete_rewards,
            text = R.string.delete_all_selected_rewards_confirmation_text,
            dismissAction = actions::onDeleteAllSelectedRewardsDialogDismissed,
            confirmAction = actions::onDeleteAllSelectedRewardsConfirmed
        )
    }
}

@Composable
private fun RewardItem(
    reward: Reward,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onRewardClicked: (reward: Reward) -> Unit,
    onItemLongClicked: (Reward) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    onRewardClicked(reward)
                },
                onLongClick = {
                    onItemLongClicked(reward)
                }
            ),
        backgroundColor = if (selected) ITtBlue.copy(alpha = PrimaryLightAlpha) else MaterialTheme.colors.surface
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
    multiSelectionModeActive: Boolean,
    selectedItemCount: Int,
    actions: RewardListActions,
) {
    TopAppBar(
        title = {
            if (multiSelectionModeActive) {
                Text(stringResource(R.string.selected_placeholder, selectedItemCount))
            } else {
                Text(stringResource(R.string.rewards))
            }
        },
        actions = {
            if (multiSelectionModeActive) {
                IconButton(onClick = actions::onDeleteAllSelectedItemsClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_all_selected_items)
                    )
                }
            } else {
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
        },
        navigationIcon = if (multiSelectionModeActive) {
            {
                IconButton(onClick = actions::onCancelMultiSelectionModeClicked) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
        } else {
            null
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
                rewardList = listOf(),
                showDeleteAllUnlockedRewardsDialog = true,
                actions = object : RewardListActions {
                    override fun onDeleteAllUnlockedRewardsClicked() {}
                    override fun onDeleteAllUnlockedRewardsConfirmed() {}
                    override fun onDeleteAllUnlockedRewardsDialogDismissed() {}
                    override fun onRewardSwiped(reward: Reward) {}
                    override fun onUndoDeleteRewardConfirmed(reward: Reward) {}
                    override fun onRewardClicked(reward: Reward) {}
                    override fun onRewardLongClicked(reward: Reward) {}
                    override fun onDeleteAllSelectedRewardsConfirmed() {}
                    override fun onDeleteAllSelectedRewardsDialogDismissed() {}
                    override fun onCancelMultiSelectionModeClicked() {}
                    override fun onDeleteAllSelectedItemsClicked() {}
                },
                selectedRewards = listOf(),
                showDeleteAllSelectedRewardsDialog = false
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
                onRewardClicked = {},
                onItemLongClicked = {},
                selected = true
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
                onRewardClicked = {},
                onItemLongClicked = {},
                selected = true
            )
        }
    }
}

