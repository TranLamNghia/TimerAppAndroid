package com.example.tlnapp_timemanagement.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.model.DailyUsage
import com.example.tlnapp_timemanagement.data.repository.DailyUsageRepository
import kotlinx.coroutines.launch

class DailyUsageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : DailyUsageRepository

    init {
        val dailyUsageDAO = AppDatabase.getDatabase(application).dailyUsageDao()
        repository = DailyUsageRepository(dailyUsageDAO)

    }

    fun insertDailyUsage(dailyUsage: DailyUsage) = viewModelScope.launch {
        repository.insertDailyUsage(dailyUsage)
    }

    fun updateDailyUsage(dailyUsage: DailyUsage) = viewModelScope.launch {
        repository.updateDailyUsage(dailyUsage)
    }

    fun getDailyUsageTime(idHistory: Int, onResult: (Long?) -> Unit) {
        viewModelScope.launch {
            val result = repository.getDailyUsageTime(idHistory)
            onResult(result)
        }
    }

    fun getDailyUsageByIdHistory(idHistory: Int, onResult: (DailyUsage?) -> Unit) {
        viewModelScope.launch {
            val result = repository.getDailyUsageByIdHistory(idHistory)
            onResult(result)
        }

    }
}