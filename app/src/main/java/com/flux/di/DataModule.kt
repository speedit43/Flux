package com.flux.di

import android.content.Context
import androidx.room.Room
import com.flux.data.dao.EventDao
import com.flux.data.dao.EventInstanceDao
import com.flux.data.dao.HabitInstanceDao
import com.flux.data.dao.HabitsDao
import com.flux.data.dao.LabelDao
import com.flux.data.dao.NotesDao
import com.flux.data.dao.SettingsDao
import com.flux.data.dao.TodoDao
import com.flux.data.dao.WorkspaceDao
import com.flux.data.database.HabitDatabase
import com.flux.data.database.HabitInstanceDatabase
import com.flux.data.database.LabelDatabase
import com.flux.data.database.NotesDatabase
import com.flux.data.database.SettingsDatabase
import com.flux.data.database.EventDatabase
import com.flux.data.database.EventInstanceDatabase
import com.flux.data.database.TodoDatabase
import com.flux.data.database.WorkspaceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideSettingsDatabase(
        @ApplicationContext app: Context
    ): SettingsDatabase = Room.databaseBuilder(
        app,
        SettingsDatabase::class.java,
        "SettingsDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideSettingsDao(db: SettingsDatabase): SettingsDao = db.dao

    @Singleton
    @Provides
    fun provideNotesDatabase(
        @ApplicationContext app: Context
    ): NotesDatabase = Room.databaseBuilder(
        app,
        NotesDatabase::class.java,
        "NotesDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideNotesDao(db: NotesDatabase): NotesDao = db.dao

    @Singleton
    @Provides
    fun provideLabelDatabase(
        @ApplicationContext app: Context
    ): LabelDatabase = Room.databaseBuilder(
        app,
        LabelDatabase::class.java,
        "LabelDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideLabelDao(db: LabelDatabase): LabelDao = db.dao

    @Singleton
    @Provides
    fun provideWorkspaceDatabase(
        @ApplicationContext app: Context
    ): WorkspaceDatabase = Room.databaseBuilder(
        app,
        WorkspaceDatabase::class.java,
        "WorkspaceDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideWorkspaceDao(db: WorkspaceDatabase): WorkspaceDao = db.dao

    @Singleton
    @Provides
    fun provideTaskDatabase(
        @ApplicationContext app: Context
    ): EventDatabase = Room.databaseBuilder(
        app,
        EventDatabase::class.java,
        "TaskDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideTaskDao(db: EventDatabase): EventDao = db.dao

    @Singleton
    @Provides
    fun provideTaskInstanceDatabase(
        @ApplicationContext app: Context
    ): EventInstanceDatabase = Room.databaseBuilder(
        app,
        EventInstanceDatabase::class.java,
        "EventInstanceDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideTaskInstanceDao(db: EventInstanceDatabase): EventInstanceDao = db.dao

    @Singleton
    @Provides
    fun provideHabitDatabase(
        @ApplicationContext app: Context
    ): HabitDatabase = Room.databaseBuilder(
        app,
        HabitDatabase::class.java,
        "HabitsDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideHabitDao(db: HabitDatabase): HabitsDao = db.dao

    @Singleton
    @Provides
    fun provideHabitInstanceDatabase(
        @ApplicationContext app: Context
    ): HabitInstanceDatabase = Room.databaseBuilder(
        app,
        HabitInstanceDatabase::class.java,
        "HabitInstanceDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideHabitInstanceDao(db: HabitInstanceDatabase): HabitInstanceDao = db.dao

    @Singleton
    @Provides
    fun provideTodoDatabase(
        @ApplicationContext app: Context
    ): TodoDatabase = Room.databaseBuilder(
        app,
        TodoDatabase::class.java,
        "TodoDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideTodoDao(db: TodoDatabase): TodoDao = db.dao
}
