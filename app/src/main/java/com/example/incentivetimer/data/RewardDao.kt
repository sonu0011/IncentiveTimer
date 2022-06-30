package com.example.incentivetimer.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Query("SELECT * FROM REWARDS")
    fun getAllRewards(): Flow<List<Reward>>

    @Query("SELECT * FROM REWARDS WHERE id = :rewardId")
    suspend fun getRewardById(rewardId: Long): Reward?

    @Delete
    suspend fun deleteReward(reward: Reward)

    @Update
    suspend fun updateReward(reward: Reward)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: Reward)
}