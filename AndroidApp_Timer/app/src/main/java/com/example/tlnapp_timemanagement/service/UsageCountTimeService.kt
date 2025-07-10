package com.example.tlnapp_timemanagement.service

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.model.DailyUsage
import com.example.tlnapp_timemanagement.data.repository.DailyUsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class UsageCountTimeService() : LifecycleService(){

    private var job : Job? = null
    private var startTime = 0L
    var userSEC : Long = 0L

    private var currentPackage : DailyUsage = DailyUsage(idHistory = 0, dateKey = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE), userSEC = 0)
//    private lateinit var timeShareViewModel : TimeShareViewModel
    private var currentIdAppPackage : Int = 0
    lateinit var repo : DailyUsageRepository

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getDatabase(applicationContext)
        repo = DailyUsageRepository(db.dailyUsageDao())

//        val timeShareViewModel = ViewModelProvider(this).get(TimeShareViewModel::class.java)
    }

    private fun onStart() {
        startTime = System.currentTimeMillis() - 1000
        job = lifecycleScope.launch(Dispatchers.IO) {
            while(isActive) {

                userSEC = repo.getDailyUsageTimeOneTime(currentIdAppPackage)
                val now = System.currentTimeMillis()
                userSEC += (now - startTime) / 1000
                Log.d("UsageCountTimeService", "Time: ${userSEC}")
                delay(1000L)
            }
        }

    }

    private fun onEnd() {
        job?.cancel()
        lifecycleScope.launch(Dispatchers.IO) {
            repo.updateTimeInDailyUsage(currentIdAppPackage, userSEC)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY

        val id = intent.getIntExtra("EXTRA_ID_PACKAGE", -1)
        if (id != -1) currentIdAppPackage = id

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