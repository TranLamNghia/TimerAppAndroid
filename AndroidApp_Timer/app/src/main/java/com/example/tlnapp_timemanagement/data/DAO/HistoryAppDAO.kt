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

    @Query("SELECT * FROM HistoryApp")
    fun getAllHistory(): HistoryApp

    @Query("SELECT * FROM HistoryApp WHERE status = :status")
    suspend fun getAppByStatus(status: String): HistoryApp?

    @Query("SELECT * FROM HistoryApp WHERE packageName = :packageName")
    fun getHistoryByPackageName(packageName: String): List<HistoryApp>

    @Query("UPDATE HistoryApp SET status = :newStatus WHERE idHistory = :idHistory")
    suspend fun updateNewStatusByIdHistory(idHistory: Int, newStatus: String)

    @Query("UPDATE HistoryApp SET idHistory = :idHistory, timeLimit = :timeLimit WHERE status = 'PENDING'")
    suspend fun updatePendingApp(idHistory: Int, timeLimit: Int)

    @Query("DELETE FROM HistoryApp WHERE status = :status")
    suspend fun deleteHistoryApp(status: String)

}