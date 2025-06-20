package com.example.tlnapp_timemanagement.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tlnapp_timemanagement.data.DAO.*
import com.example.tlnapp_timemanagement.data.model.*

@Database(entities = [HistoryApp :: class, DailyUsage :: class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyAppDao(): HistoryAppDAO
    abstract fun dailyUsageDao(): DailyUsageDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context : Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
