package com.example.tlnapp_timemanagement.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "DailyUsage",
    primaryKeys = ["idHistory","dateKey"],
    foreignKeys = [ForeignKey(
        entity         = HistoryApp::class,
        parentColumns  = ["idHistory"],
        childColumns   = ["idHistory"],
        onDelete       = ForeignKey.NO_ACTION
    )])
data class DailyUsage (
    val idHistory: Int,
    val dateKey: String,
    val userSEC: Int

)