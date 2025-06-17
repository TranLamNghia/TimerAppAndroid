package com.example.tlnapp_timemanagement.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tlnapp_timemanagement.data.DAO.*
import com.example.tlnapp_timemanagement.data.model.*

@Database(entities = [TrackedApp :: class, HistoryApp :: class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackedAppDao(): TrackedAppDAO
    abstract fun historyAppDao(): HistoryAppDAO
}
