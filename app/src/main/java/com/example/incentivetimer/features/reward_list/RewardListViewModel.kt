package com.example.incentivetimer.features.reward_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.incentivetimer.data.RewardDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RewardListViewModel @Inject constructor(
    private val rewardDao : RewardDao
) : ViewModel() {
    val rewards  = rewardDao.getAllRewards().asLiveData()

}