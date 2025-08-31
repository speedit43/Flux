package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

enum class Repetition {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}

enum class EventStatus {
    PENDING, COMPLETED
}

@Entity
data class EventModel(
    @PrimaryKey
    val eventId: String = UUID.randomUUID().toString(),
    val workspaceId: String = "",
    val title: String = "",
    val description: String = "",
    val repetition: Repetition = Repetition.NONE,
    val isAllDay: Boolean = false,
    val notificationOffset: Long = 0L,
    val startDateTime: Long = System.currentTimeMillis(),
)

@Entity(primaryKeys = ["eventId", "instanceDate"])
data class EventInstanceModel(
    val eventId: String = "",
    val workspaceId: String = "",
    val instanceDate: LocalDate = LocalDate.now(),
    val status: EventStatus = EventStatus.PENDING
)