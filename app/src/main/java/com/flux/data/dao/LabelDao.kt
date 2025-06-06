package com.flux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flux.data.model.LabelModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertLabel(label: LabelModel)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun upsertLabels(labels: List<LabelModel>)

    @Delete
    suspend fun deleteLabel(label: LabelModel)

    @Query("SELECT * FROM LabelModel")
    fun loadAllLabels(): Flow<List<LabelModel>>
}
