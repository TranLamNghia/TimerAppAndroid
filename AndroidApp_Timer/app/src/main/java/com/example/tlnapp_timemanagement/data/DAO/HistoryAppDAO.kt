package com.example.tlnapp_timemanagement.data.DAO

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tlnapp_timemanagement.data.model.HistoryApp

interface HistoryAppDAO {
    @Insert
    fun insert(historyApp: HistoryApp)

    @Update
    fun update(historyApp: HistoryApp)

    @Delete
    fun delete(historyApp: HistoryApp)

    @Query("SELECT * FROM HistoryApp")
    fun getAllHistory(packageName: String): HistoryApp?

    @Query("SELECT * FROM HistoryApp WHERE packageName = :packageName ORDER BY beginTime DESC")
    suspend fun getHistoryByPackageName(packageName: String): List<HistoryApp>
}