package com.flux.di

import android.content.Context
import androidx.room.Room
import com.flux.data.dao.LabelDao
import com.flux.data.dao.NotesDao
import com.flux.data.dao.SettingsDao
import com.flux.data.database.LabelDatabase
import com.flux.data.database.NotesDatabase
import com.flux.data.database.SettingsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}
