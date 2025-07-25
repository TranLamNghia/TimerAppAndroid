package com.example.tlnapp_timemanagement.data.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tlnapp_timemanagement.data.model.DailyUsage

@Dao
interface DailyUsageDAO {
    @Insert
    suspend fun insert(dailyUsage: DailyUsage)

    @Update
    suspend fun update(dailyUsage: DailyUsage)

    @Query("DELETE FROM DAILYUSAGE")
    suspend fun deleteAll()

    @Query("SELECT * FROM DAILYUSAGE WHERE idHistory = :idHistory")
    suspend fun getDailyUsageByIdHistory(idHistory: Int): DailyUsage

    @Query("SELECT DAILYUSAGE.idHistory, dateKey, userSEC FROM DAILYUSAGE, HISTORYAPP WHERE DAILYUSAGE.idHistory = HISTORYAPP.idHistory AND HISTORYAPP.packageName = :packageName")
    suspend fun getDailyUsageByPackageName(packageName: String): DailyUsage

    @Query("UPDATE DAILYUSAGE SET dateKey = :dateKey, userSEC = 0 WHERE idHistory = :idHistory")
    suspend fun resetNewDailyUsage(idHistory: Int, dateKey: String)

    @Query("UPDATE DAILYUSAGE SET userSEC = :userSEC WHERE idHistory = :idHistory")
    suspend fun updateTimeInDailyUsage(idHistory: Int, userSEC: Long)

    @Transaction
    suspend fun insertAndReturnId(dailyUsage: DailyUsage): Int {
        insert(dailyUsage)
        return dailyUsage.idHistory
    }

    @Query("SELECT DAILYUSAGE.idHistory FROM DAILYUSAGE, HISTORYAPP WHERE DAILYUSAGE.idHistory = HISTORYAPP.idHistory AND HISTORYAPP.packageName = :packageName")
    suspend fun getIdHistoryEXISTS(packageName: String): Int?

    @Query("SELECT userSEC FROM DAILYUSAGE WHERE idHistory = :idHistory")
    fun getDailyUsageTime(idHistory: Int): LiveData<Long>

    @Query("SELECT userSEC FROM DAILYUSAGE WHERE idHistory = :idHistory")
    suspend fun getDailyUsageTimeOneTime(idHistory: Int): Long
}