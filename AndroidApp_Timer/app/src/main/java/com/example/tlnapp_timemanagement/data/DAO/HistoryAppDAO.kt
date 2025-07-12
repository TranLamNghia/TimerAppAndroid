package com.example.tlnapp_timemanagement.data.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tlnapp_timemanagement.data.model.HistoryApp

@Dao
interface HistoryAppDAO {
    @Insert
    suspend fun insert(historyApp: HistoryApp)

    @Update
    suspend fun update(historyApp: HistoryApp)

    @Delete
    suspend fun delete(historyApp: HistoryApp)

    @Query("SELECT * FROM HistoryApp WHERE status = :status")
    suspend fun getAppByStatus(status: String): HistoryApp?

    @Query("SELECT * FROM HistoryApp WHERE idHistory = (SELECT MAX(idHistory) FROM HistoryApp)")
    fun getAppByMaxIdLive(): LiveData<HistoryApp>

    @Query("SELECT * FROM HistoryApp WHERE packageName = :packageName")
    fun getHistoryByPackageName(packageName: String): List<HistoryApp>

    @Query("SELECT * FROM HistoryApp WHERE idHistory = :idHistory")
    suspend fun getHistoryById(idHistory: Int): HistoryApp

    @Query("SELECT timeLimit FROM HistoryApp WHERE idHistory = :idHistory")
    suspend fun getTimeLimit(idHistory: Int): Int

    @Query("UPDATE HistoryApp SET status = :newStatus WHERE idHistory = :idHistory")
    suspend fun updateNewStatusByIdHistory(idHistory: Int, newStatus: String)

    @Query("UPDATE HistoryApp SET packageName = :packageName, timeLimit = :timeLimit WHERE idHistory = :idHistory")
    suspend fun updateApp(idHistory: Int, packageName: String, timeLimit: Int)

    @Query("DELETE FROM HistoryApp WHERE status = :status")
    suspend fun deleteHistoryApp(status: String)


}