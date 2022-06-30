package com.example.incentivetimer.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.incentivetimer.core.ui.IconKey

@Entity(tableName = "rewards")
data class Reward(
    val iconKey: IconKey,
    val title: String,
    val chanceInPercent: Int,
    val isUnlocked: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)
