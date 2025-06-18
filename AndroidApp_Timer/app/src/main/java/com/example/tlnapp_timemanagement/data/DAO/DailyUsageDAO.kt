package com.example.tlnapp_timemanagement.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.example.tlnapp_timemanagement.data.model.DailyUsage

@Dao
interface DailyUsageDAO {
    @Insert
    suspend fun insert(dailyUsage: DailyUsage)

    @Update
    suspend fun update(dailyUsage: DailyUsage)

}