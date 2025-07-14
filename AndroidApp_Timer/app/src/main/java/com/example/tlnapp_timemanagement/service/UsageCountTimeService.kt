package com.example.tlnapp_timemanagement.service

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.repository.DailyUsageRepository
import com.example.tlnapp_timemanagement.data.repository.HistoryAppRepository
import com.example.tlnapp_timemanagement.dialog.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class UsageCountTimeService() : LifecycleService(){

    private var job : Job? = null
    private var startTime = 0L
    var userSEC : Long = 0L
    var timeLimit : Long = 0L

    private var currentIdAppPackage : Int = 0
    private var inputIdAppPackage : Int = 0

    lateinit var dailyUsageRepository : DailyUsageRepository
    lateinit var historyAppRepository: HistoryAppRepository

    var notificationProgress : Boolean = false
    val valueProgress = 0.5

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getDatabase(applicationContext)
        dailyUsageRepository = DailyUsageRepository(db.dailyUsageDao())
        historyAppRepository = HistoryAppRepository(db.historyAppDao(), db.dailyUsageDao())

        notificationProgress = false
    }

    private fun onStart() {
        if (inputIdAppPackage != currentIdAppPackage) {
            notificationProgress = false
            currentIdAppPackage = inputIdAppPackage
        }
        startTime = System.currentTimeMillis() - 1000
        job = lifecycleScope.launch(Dispatchers.IO) {
            timeLimit = historyAppRepository.getTimeLimit(currentIdAppPackage) * 60 * 1000L
            while(isActive) {
                userSEC = dailyUsageRepository.getDailyUsageTimeOneTime(currentIdAppPackage)
                val now = System.currentTimeMillis()
                userSEC += (now - startTime) / 1000
                Log.d("UsageCountTimeService", "Time: ${userSEC}")

                if (userSEC >= timeLimit * valueProgress && notificationProgress == false) {
                    launch {
                        notificationProgress = true
                        Notification.showProgressNotification(applicationContext, "App Timer", "Bạn đã sử dụng 50% thời gian", 50)
                    }
                }
                if(userSEC == timeLimit - 60000) {
                    Notification.showSimpleNotification_HighPriority(applicationContext, "App Timer", "Bạn còn 1 phút sử dụng")
                }
                if (userSEC >= timeLimit) {
                    FocusDetectService.instance?.redirectToHome()
                    Notification.showSimpleNotification(applicationContext, "App Timer", "Bạn đã sử dụng hết thời gian")
                    break
                }
                delay(1000L)
            }
        }

    }

    private fun onEnd() {
        job?.cancel()
        lifecycleScope.launch(Dispatchers.IO) {
            if (userSEC >= timeLimit.toLong()) {
                dailyUsageRepository.updateTimeInDailyUsage(currentIdAppPackage, timeLimit.toLong())
            }
            else dailyUsageRepository.updateTimeInDailyUsage(currentIdAppPackage, userSEC)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY

        val id = intent.getIntExtra("EXTRA_ID_PACKAGE", -1)
        if (id != -1) inputIdAppPackage = id

        when(intent.action) {
            "START" -> {
                onStart()
            }
            "END" -> {
                onEnd()
            }
        }
        return START_STICKY
    }
}