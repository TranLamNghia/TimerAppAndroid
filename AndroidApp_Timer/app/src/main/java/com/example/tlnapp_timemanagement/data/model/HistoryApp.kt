package com.example.tlnapp_timemanagement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName   = "HistoryApp")
data class HistoryApp(
    @PrimaryKey(autoGenerate = true) val idHistory: Int,
    val packageName: String,
    val timeLimit: Int,
    val beginTime: Long?,
    val endTime: Long?,
    val status: String
)