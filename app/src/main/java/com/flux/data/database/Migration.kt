package com.flux.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. Create new table with new schema
        database.execSQL("""
            CREATE TABLE WorkspaceModel_new (
                workspaceId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                colorInd INTEGER NOT NULL,
                cover TEXT NOT NULL,
                icon INTEGER NOT NULL,
                passKey TEXT NOT NULL,
                isPinned INTEGER NOT NULL,
                selectedSpaces TEXT NOT NULL
            )
        """)

        // 2. Copy old data into new table
        val cursor = database.query("""
            SELECT workspaceId, title, description, colorInd, cover, icon, passKey, isPinned,
                   isNotesAdded, isJournalAdded, isTodoAdded, isEventsAdded, isCalenderAdded, isHabitsAdded, isAnalyticsAdded
            FROM WorkspaceModel
        """)

        while (cursor.moveToNext()) {
            val workspaceId = cursor.getLong(0)
            val title = cursor.getString(1)
            val description = cursor.getString(2)
            val colorInd = cursor.getInt(3)
            val cover = cursor.getString(4)
            val icon = cursor.getInt(5)
            val passKey = cursor.getString(6)
            val isPinned = cursor.getInt(7)

            val selected = mutableListOf<Int>()
            if (cursor.getInt(8) == 1) selected.add(1) // Notes
            if (cursor.getInt(9) == 1) selected.add(5) // Journal
            if (cursor.getInt(10) == 1) selected.add(2) // To-Do
            if (cursor.getInt(11) == 1) selected.add(3) // Events
            if (cursor.getInt(12) == 1) selected.add(4) // Calendar
            if (cursor.getInt(13) == 1) selected.add(6) // Habits
            if (cursor.getInt(14) == 1) selected.add(7) // Analytics

            val selectedStr = selected.joinToString(",")

            database.execSQL(
                """
                INSERT INTO WorkspaceModel_new 
                (workspaceId, title, description, colorInd, cover, icon, passKey, isPinned, selectedSpaces) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                arrayOf(workspaceId, title, description, colorInd, cover, icon, passKey, isPinned, selectedStr)
            )
        }
        cursor.close()

        // 3. Drop old table
        database.execSQL("DROP TABLE WorkspaceModel")

        // 4. Rename new table to old name
        database.execSQL("ALTER TABLE WorkspaceModel_new RENAME TO WorkspaceModel")
    }
}