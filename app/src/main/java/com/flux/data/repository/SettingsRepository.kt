package com.flux.data.repository

import com.flux.data.model.SettingsModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun upsertSettings(settings: SettingsModel)
    fun loadSettings(): Flow<SettingsModel?>
}