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
        val dailyUsageDao = AppDatabase.getDatabase(application).dailyUsageDao()
        repository = HistoryAppRepository(historyAppDao, dailyUsageDao)
    }

    fun insertHistory(historyApp: HistoryApp) = viewModelScope.launch {
        repository.insertHistory(historyApp)
    }

    fun updateHistory(historyApp: HistoryApp) = viewModelScope.launch {
        repository.updateHistory(historyApp)
    }

    fun updateApp(idHistory: Int, packageName: String, timeLimit: Int) = viewModelScope.launch {
        repository.updateApp(idHistory, packageName, timeLimit)
    }

    fun updateNewStatusByIdHistory(idHistory:Int, newStatus: String) = viewModelScope.launch {
        repository.updateNewStatusByIdHistory(idHistory,newStatus)
    }

    fun deleteHistory(historyApp: HistoryApp) = viewModelScope.launch {
        repository.deleteHistory(historyApp)
    }

    fun deleteHistoryApp(status: String) = viewModelScope.launch {
        repository.deleteHistoryApp(status)
    }

    fun getAppByMaxIdLive(): LiveData<HistoryApp> = repository.getAppByMaxIdLive()

    suspend fun getHistoryById(idHistory: Int) : HistoryApp = repository.getHistoryById(idHistory)

    suspend fun getPendingApp2(): HistoryApp? {
        return repository.getAppByStatus("PENDING")
    }

    suspend fun getActiveApp2(): HistoryApp? {
        return repository.getAppByStatus("ACTIVE")
    }

    // Bất đồng bộ
    fun getPendingApp(onResult: (HistoryApp?) -> Unit) {
        viewModelScope.launch {
            val result = repository.getAppByStatus("PENDING")
            onResult(result)
        }
    }
    // Bất đồng bộ
    fun getActiveApp(onResult: (HistoryApp?) -> Unit) {
        viewModelScope.launch {
            val result = repository.getAppByStatus("ACTIVE")
            onResult(result)
        }
    }

}