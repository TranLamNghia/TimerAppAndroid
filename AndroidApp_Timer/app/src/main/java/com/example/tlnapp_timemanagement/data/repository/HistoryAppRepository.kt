package com.example.tlnapp_timemanagement.data.repository

import androidx.lifecycle.LiveData
import com.example.tlnapp_timemanagement.data.DAO.HistoryAppDAO
import com.example.tlnapp_timemanagement.data.model.HistoryApp

class HistoryAppRepository(private val historyAppDao: HistoryAppDAO) {

    suspend fun insertHistory(historyApp: HistoryApp) = historyAppDao.insert(historyApp)
    suspend fun updateHistory(historyApp: HistoryApp) = historyAppDao.update(historyApp)
    suspend fun deleteHistory(historyApp: HistoryApp) = historyAppDao.delete(historyApp)

    fun getAppByStatus(status: String) : HistoryApp? = historyAppDao.getAppByStatus(status)

    fun getAllHistory() = historyAppDao.getAllHistory()

}