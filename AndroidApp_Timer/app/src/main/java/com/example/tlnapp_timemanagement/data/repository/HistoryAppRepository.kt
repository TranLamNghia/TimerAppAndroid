package com.example.tlnapp_timemanagement.data.repository

import androidx.lifecycle.LiveData
import com.example.tlnapp_timemanagement.data.DAO.DailyUsageDAO
import com.example.tlnapp_timemanagement.data.DAO.HistoryAppDAO
import com.example.tlnapp_timemanagement.data.model.DailyUsage
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HistoryAppRepository(private val historyAppDao: HistoryAppDAO, private val dailyUsageDao: DailyUsageDAO) {


    suspend fun insertHistory(historyApp: HistoryApp) = historyAppDao.insert(historyApp)
    suspend fun updateHistory(historyApp: HistoryApp) = historyAppDao.update(historyApp)
    suspend fun deleteHistory(historyApp: HistoryApp) = historyAppDao.delete(historyApp)
    suspend fun updateApp(idHistory: Int, packageName: String, timeLimit: Int) = historyAppDao.updateApp(idHistory, packageName, timeLimit)
    suspend fun updateNewStatusByIdHistory(idHistory:Int, newStatus : String) = historyAppDao.updateNewStatusByIdHistory(idHistory,newStatus)

    suspend fun getAppByStatus(status: String) : HistoryApp? = historyAppDao.getAppByStatus(status)
    fun getAppByMaxIdLive(): LiveData<HistoryApp> = historyAppDao.getAppByMaxIdLive()
    suspend fun getHistoryById(idHistory: Int) : HistoryApp = historyAppDao.getHistoryById(idHistory)

    suspend fun deleteHistoryApp(status: String) = historyAppDao.deleteHistoryApp(status)

    suspend fun onAppForeground(currentApp: HistoryApp) {
        val now = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        val activeApp = historyAppDao.getAppByStatus("ACTIVE")

        val daily = DailyUsage(
            idHistory = currentApp.idHistory,
            dateKey = now,
            userSEC = 0
        )
        if (dailyUsageDao.getIdHistoryEXISTS(historyAppDao.getHistoryById(daily.idHistory).packageName) == null) {
            dailyUsageDao.insert(daily)
        }
        if (currentApp.status.equals("PENDING")) {
            if (activeApp!= null) {
                historyAppDao.updateNewStatusByIdHistory(activeApp.idHistory , "INACTIVE")
            }
            historyAppDao.updateNewStatusByIdHistory(currentApp.idHistory , "ACTIVE")
        }
    }

    suspend fun onAppSessionEnd(currentApp: HistoryApp) {

    }

    suspend fun getTimeLimit(idHistory: Int) = historyAppDao.getTimeLimit(idHistory)

}