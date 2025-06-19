package com.example.tlnapp_timemanagement.data.repository

import androidx.lifecycle.LiveData
import com.example.tlnapp_timemanagement.data.DAO.HistoryAppDAO
import com.example.tlnapp_timemanagement.data.model.HistoryApp

class HistoryAppRepository(private val historyAppDao: HistoryAppDAO) {

    suspend fun insertHistory(historyApp: HistoryApp) = historyAppDao.insert(historyApp)
    suspend fun updateHistory(historyApp: HistoryApp) = historyAppDao.update(historyApp)
    suspend fun deleteHistory(historyApp: HistoryApp) = historyAppDao.delete(historyApp)
    suspend fun updatePendingApp(package_name: String, timeLimit: Int) = historyAppDao.updatePendingApp(package_name, timeLimit)
    suspend fun updateStatusForPending(newStatus : String) = historyAppDao.updateStatusForPending(newStatus)

    suspend fun getAppByStatus(status: String) : HistoryApp? = historyAppDao.getAppByStatus(status)

    fun getAllHistory() = historyAppDao.getAllHistory()

}