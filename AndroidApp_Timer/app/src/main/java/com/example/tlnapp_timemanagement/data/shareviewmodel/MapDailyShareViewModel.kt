package com.example.tlnapp_timemanagement.data.shareviewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MapDailyShareViewModel : ViewModel() {

    private val _mapDailyUsage = MutableStateFlow<Map<Int, String>>(emptyMap())
    val mapDailyUsage: MutableStateFlow<Map<Int, String>> = _mapDailyUsage

    fun putApp(idHistory: Int, packageName: String) {
        _mapDailyUsage.value = _mapDailyUsage.value.toMutableMap().apply {
            put(idHistory, packageName)
        }
    }

}