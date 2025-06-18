package com.example.tlnapp_timemanagement.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.repository.HistoryAppRepository
import kotlinx.coroutines.launch

class HistoryAppViewModel(application: Application): AndroidViewModel(application) {
    private val repository : HistoryAppRepository

    init {
        val historyAppDao = AppDatabase.getDatabase(application).historyAppDao()
        repository = HistoryAppRepository(historyAppDao)
    }

    fun insertHistory(historyApp: HistoryApp) = viewModelScope.launch {
        repository.insertHistory(historyApp)
    }

    fun updateHistory(historyApp: HistoryApp) = viewModelScope.launch {
        repository.updateHistory(historyApp)
    }

    fun deleteHistory(historyApp: HistoryApp) = viewModelScope.launch {
        repository.deleteHistory(historyApp)
    }

    fun getPendingApps(): HistoryApp? = repository.getAppByStatus("PENDING")
    fun getActiveApps(): HistoryApp? = repository.getAppByStatus("ACTIVE")

}