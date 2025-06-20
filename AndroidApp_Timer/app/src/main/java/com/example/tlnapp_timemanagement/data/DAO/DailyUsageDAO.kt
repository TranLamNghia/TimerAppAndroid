package com.example.tlnapp_timemanagement.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tlnapp_timemanagement.data.model.DailyUsage

@Dao
interface DailyUsageDAO {
    @Insert
    suspend fun insert(dailyUsage: DailyUsage)

    @Update
    suspend fun update(dailyUsage: DailyUsage)

    @Query("SELECT * FROM DAILYUSAGE WHERE idHistory = :idHistory")
    suspend fun getDailyUsageByIdHistory(idHistory: String): DailyUsage

    @Query("UPDATE DAILYUSAGE SET dateKey = :dateKey, userSEC = 0 WHERE idHistory = :idHistory")
    suspend fun resetNewDailyUsage(idHistory: Int, dateKey: String)

}