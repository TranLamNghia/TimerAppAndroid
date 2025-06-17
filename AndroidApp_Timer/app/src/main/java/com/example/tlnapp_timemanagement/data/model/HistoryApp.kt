package com.example.tlnapp_timemanagement.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName   = "HistoryApp",
    foreignKeys = [ForeignKey(
        entity         = TrackedApp::class,
        parentColumns  = ["packageName"],
        childColumns   = ["packageName"],
        onDelete       = ForeignKey.NO_ACTION      // vì bạn KHÔNG xoá TrackedApp
    )]
)

data class HistoryApp(
    @PrimaryKey(autoGenerate = true) val idHistory: Int,
    val packageName: String,
    val beginTime: Long,
    val endTime: Long?,
    val Status: String

)