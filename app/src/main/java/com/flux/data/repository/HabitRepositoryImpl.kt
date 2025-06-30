package com.flux.data.repository

import com.flux.data.dao.HabitInstanceDao
import com.flux.data.dao.HabitsDao
import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel
import com.flux.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitsDao,
    private val instanceDao: HabitInstanceDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): HabitRepository  {
    override suspend fun upsertHabit(habit: HabitModel) { return withContext(ioDispatcher) { dao.upsertHabit(habit) } }
    override suspend fun deleteInstance(habitInstance: HabitInstanceModel) { return withContext(ioDispatcher) { instanceDao.deleteInstance(habitInstance) } }
    override suspend fun upsertHabitInstance(habitInstance: HabitInstanceModel) { return withContext(ioDispatcher) { instanceDao.upsertInstance(habitInstance) } }
    override fun loadAllHabitInstance(workspaceId: Int): Flow<List<HabitInstanceModel>> { return instanceDao.loadAllInstances(workspaceId) }
    override fun loadAllHabits(workspaceId: Int): Flow<List<HabitModel>> { return dao.loadAllHabits(workspaceId) }
    override suspend fun deleteHabit(habit: HabitModel) { return withContext(ioDispatcher) {
        instanceDao.deleteAllInstances(habit.habitId)
        dao.deleteHabit(habit) }
    }
    override suspend fun deleteAllWorkspaceHabit(workspaceId: Int) {
        return withContext(ioDispatcher) {
            dao.deleteAllWorkspaceHabit(workspaceId)
            instanceDao.deleteAllWorkspaceInstance(workspaceId)
        }
    }
}