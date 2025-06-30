package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class HabitModel (
    @PrimaryKey(autoGenerate = true)
    val habitId: Int = 0,
    val workspaceId: Int = 0,
    val title: String = "",
    val description: String = "",
    val bestStreak: Long = 0L,
    val startDateTime: Long = System.currentTimeMillis()
)

@Entity(primaryKeys = ["habitId", "instanceDate"])
data class HabitInstanceModel(
    val habitId: Int=0,
    val workspaceId: Int=0,
    val instanceDate: LocalDate= LocalDate.now()
)