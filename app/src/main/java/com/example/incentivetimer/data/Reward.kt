package com.example.incentivetimer.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.incentivetimer.core.ui.IconKey
import com.example.incentivetimer.core.ui.defaultRewardIcon
import kotlinx.parcelize.Parcelize

@Entity(tableName = "rewards")
@Parcelize
data class Reward(
    val iconKey: IconKey,
    val name: String,
    val chanceInPercent: Int,
    val isUnlocked: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {
    companion object {
        val DEFAULT = Reward(defaultRewardIcon, "", 10)
    }
}
