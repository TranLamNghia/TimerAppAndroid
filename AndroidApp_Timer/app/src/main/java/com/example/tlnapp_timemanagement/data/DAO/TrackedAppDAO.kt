package com.example.tlnapp_timemanagement.data.DAO

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tlnapp_timemanagement.data.model.TrackedApp

interface TrackedAppDAO {
    @Insert
    fun insert(trackedApp: TrackedApp)

    @Update
    fun update(trackedApp: TrackedApp)

    @Query("SELECT * FROM TrackedApp, HistoryApp WHERE TrackedApp.packageName = HistoryApp.packageName AND HistoryApp.endTime IS NULL")
    fun getCurrentApp(): TrackedApp?
}