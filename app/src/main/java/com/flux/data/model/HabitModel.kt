package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity
data class HabitModel(
    @PrimaryKey
    val habitId: String = UUID.randomUUID().toString(),
    val workspaceId: String = "",
    val title: String = "",
    val description: String = "",
    val bestStreak: Long = 0L,
    val startDateTime: Long = System.currentTimeMillis()
)

@Entity(primaryKeys = ["habitId", "instanceDate"])
data class HabitInstanceModel(
    val habitId: String = "",
    val workspaceId: String = "",
    val instanceDate: LocalDate = LocalDate.now()
)