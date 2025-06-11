package com.flux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkspaceModel(
    @PrimaryKey(autoGenerate = true)
    val workspaceId: Int = 0,
    val title: String="",
    val description: String="",
    val colorInd: Int=0,
    val isNotesAdded: Boolean=false,
    val isTaskAdded: Boolean=false,
    val isCalenderAdded: Boolean=false,
    val isAnalyticsAdded: Boolean=false,
)
