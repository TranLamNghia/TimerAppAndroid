package com.example.tlnapp_timemanagement.data.repository

import com.example.tlnapp_timemanagement.data.DAO.DailyUsageDAO
import com.example.tlnapp_timemanagement.data.model.DailyUsage

class DailyUsageRepository(private val dailyUsageDAO: DailyUsageDAO) {
    suspend fun insertDailyUsage(dailyUsage: DailyUsage) = dailyUsageDAO.insert(dailyUsage)

    suspend fun updateDailyUsage(dailyUsage: DailyUsage) = dailyUsageDAO.update(dailyUsage)

    suspend fun getDailyUsageByIdHistory(idHistory: Int): DailyUsage = dailyUsageDAO.getDailyUsageByIdHistory(idHistory)

    suspend fun resetNewDailyUsage(idHistory: Int, dateKey: String) = dailyUsageDAO.resetNewDailyUsage(idHistory, dateKey)

    suspend fun updateTimeInDailyUsage(idHistory : Int, deltaSec : Long) = dailyUsageDAO.updateTimeInDailyUsage(idHistory, deltaSec)
}