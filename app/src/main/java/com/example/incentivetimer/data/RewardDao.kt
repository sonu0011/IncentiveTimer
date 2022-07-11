package com.example.incentivetimer.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Query("SELECT * FROM REWARDS order by isUnlocked DESC")
    fun getAllRewardsSortedByIsUnlocked(): Flow<List<Reward>>

    @Query("SELECT * FROM REWARDS WHERE isUnlocked =0")
    fun getAllUnlockedRewards(): Flow<List<Reward>>

    @Query("SELECT * FROM REWARDS WHERE id = :rewardId")
    fun getRewardById(rewardId: Long?): Flow<Reward?>

    @Delete
    suspend fun deleteReward(reward: Reward)

    @Update
    suspend fun updateReward(reward: Reward)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: Reward)

    @Delete
    suspend fun deleteRewards(rewards: List<Reward>)

    @Query("DELETE FROM rewards WHERE isUnlocked = 1")
    suspend fun deleteAllUnlockedRewards()
}