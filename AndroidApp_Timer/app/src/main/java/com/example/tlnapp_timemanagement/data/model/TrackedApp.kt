package com.example.tlnapp_timemanagement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackedApp(
    @PrimaryKey val packageName: String,
    val timeLimit: Int
)
