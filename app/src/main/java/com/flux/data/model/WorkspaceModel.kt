package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkspaceModel(
    @PrimaryKey(autoGenerate = true)
    val workspaceId: Long = 0L,
    val title: String="",
    val description: String="",
    val colorInd: Int=0,
    val cover: String="",
    val icon: Int=48,
    val passKey: String="",
    val isPinned: Boolean=false,
    val isNotesAdded: Boolean=false,
    val isJournalAdded: Boolean=false,
    val isTodoAdded: Boolean=false,
    val isEventsAdded: Boolean=false,
    val isCalenderAdded: Boolean=false,
    val isHabitsAdded: Boolean=false,
    val isAnalyticsAdded: Boolean=false,
)
