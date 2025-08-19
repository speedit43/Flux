package com.flux.data.repository

import com.flux.data.dao.SettingsDao
import com.flux.data.model.SettingsModel
import com.flux.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dao: SettingsDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : SettingsRepository {
    override suspend fun upsertSettings(settings: SettingsModel) {
        return withContext(ioDispatcher) { dao.upsertSettings(settings) }
    }

    override fun loadSettings(): Flow<SettingsModel?> {
        return dao.loadSettings()
    }
}