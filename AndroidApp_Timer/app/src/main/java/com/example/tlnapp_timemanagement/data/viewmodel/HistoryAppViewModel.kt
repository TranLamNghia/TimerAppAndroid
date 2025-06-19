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

    fun updatePendingApp(package_name: String, timeLimit: Int) = viewModelScope.launch {
        repository.updatePendingApp(package_name, timeLimit)
    }

    fun updateStatusForPending(newStatus: String) = viewModelScope.launch {
        repository.updateStatusForPending(newStatus)
    }

    fun deleteHistory(historyApp: HistoryApp) = viewModelScope.launch {
        repository.deleteHistory(historyApp)
    }

    fun getPendingApp(onResult: (HistoryApp?) -> Unit) {
        viewModelScope.launch {
            val result = repository.getAppByStatus("PENDING")
            onResult(result)
        }
    }

    fun getActiveApp(onResult: (HistoryApp?) -> Unit) {
        viewModelScope.launch {
            val result = repository.getAppByStatus("ACTIVE")
            onResult(result)
        }
    }

}