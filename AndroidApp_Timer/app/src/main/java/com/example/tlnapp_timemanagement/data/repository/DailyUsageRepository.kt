package com.example.tlnapp_timemanagement.data.repository

import androidx.lifecycle.LiveData
import com.example.tlnapp_timemanagement.data.DAO.DailyUsageDAO
import com.example.tlnapp_timemanagement.data.model.DailyUsage

class DailyUsageRepository(private val dailyUsageDAO: DailyUsageDAO) {
    suspend fun insertDailyUsage(dailyUsage: DailyUsage) = dailyUsageDAO.insert(dailyUsage)

    suspend fun updateDailyUsage(dailyUsage: DailyUsage) = dailyUsageDAO.update(dailyUsage)

    suspend fun getDailyUsageByIdHistory(idHistory: Int): DailyUsage = dailyUsageDAO.getDailyUsageByIdHistory(idHistory)

    suspend fun resetNewDailyUsage(idHistory: Int, dateKey: String) = dailyUsageDAO.resetNewDailyUsage(idHistory, dateKey)

    suspend fun updateTimeInDailyUsage(idHistory: Int, userSEC : Long) = dailyUsageDAO.updateTimeInDailyUsage(idHistory, userSEC)

    fun getDailyUsageTime(idHistory: Int): LiveData<Long> = dailyUsageDAO.getDailyUsageTime(idHistory)

    suspend fun getDailyUsageTimeOneTime(idHistory: Int) : Long = dailyUsageDAO.getDailyUsageTimeOneTime(idHistory)
}