package com.example.tlnapp_timemanagement.data.repository

import androidx.lifecycle.LiveData
import com.example.tlnapp_timemanagement.data.DAO.DailyUsageDAO
import com.example.tlnapp_timemanagement.data.DAO.HistoryAppDAO
import com.example.tlnapp_timemanagement.data.model.DailyUsage
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import java.time.Instant

class HistoryAppRepository(private val historyAppDao: HistoryAppDAO, private val dailyUsageDao: DailyUsageDAO) {

    suspend fun insertHistory(historyApp: HistoryApp) = historyAppDao.insert(historyApp)
    suspend fun updateHistory(historyApp: HistoryApp) = historyAppDao.update(historyApp)
    suspend fun deleteHistory(historyApp: HistoryApp) = historyAppDao.delete(historyApp)
    suspend fun updatePendingApp(idHistory: Int, timeLimit: Int) = historyAppDao.updatePendingApp(idHistory, timeLimit)
    suspend fun updateNewStatusByIdHistory(idHistory:Int, newStatus : String) = historyAppDao.updateNewStatusByIdHistory(idHistory,newStatus)

    suspend fun getAppByStatus(status: String) : HistoryApp? = historyAppDao.getAppByStatus(status)
    suspend fun deleteHistoryApp(status: String) = historyAppDao.deleteHistoryApp(status)

    fun getAllHistory() = historyAppDao.getAllHistory()

    suspend fun onAppForeground(pkg: String, now: Instant?) {
        var currentApp = historyAppDao.getAppByStatus("PENDING")
        if (currentApp == null) {
            currentApp = historyAppDao.getAppByStatus("ACTIVE")
        }
        if (currentApp == null) {

        } else {
            if (currentApp.packageName == pkg) {
                if (currentApp.status.equals("PENDING")) {
                    val daily = DailyUsage(
                        idHistory = currentApp.idHistory,
                        dateKey = now.toString(),
                        userSEC = 0
                    )
                    dailyUsageDao.insert(daily)
                    val activeApp = historyAppDao.getAppByStatus("ACTIVE")
                    if (activeApp!= null) {
                        historyAppDao.updateNewStatusByIdHistory(activeApp.idHistory , "INACTIVE")
                    }
                } else {
                    dailyUsageDao.resetNewDailyUsage(currentApp.idHistory, now.toString())
                }
            }
        }

    }

}