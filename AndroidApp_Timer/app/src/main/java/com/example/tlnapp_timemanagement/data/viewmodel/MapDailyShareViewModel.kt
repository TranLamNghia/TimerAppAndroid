package com.example.tlnapp_timemanagement.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow

class MapDailyShareViewModel() : ViewModel() {

    private val _mapDailyUsage = MutableStateFlow<Map<Int, String>>(emptyMap())
    val mapDailyUsage: MutableStateFlow<Map<Int, String>> = _mapDailyUsage

    fun putApp(idHistory: Int, packageName: String) {
        _mapDailyUsage.value = _mapDailyUsage.value.toMutableMap().apply {
            put(idHistory, packageName)
        }
    }

}