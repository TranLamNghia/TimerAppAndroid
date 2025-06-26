package com.example.tlnapp_timemanagement.service

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.tlnapp_timemanagement.data.model.DailyUsage
import com.example.tlnapp_timemanagement.data.repository.DailyUsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class UsageCountTimeService() : LifecycleService(){

    private var job : Job? = null
    private var startTime = 0L
    private val _elapsed = MutableStateFlow(0L)
    private var elapsed : StateFlow<Long> = _elapsed

    private var currentPackage : DailyUsage = DailyUsage(idHistory = 0, dateKey = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE), userSEC = 0)
//    private val currentPackage = currentPackage
    lateinit var repo : DailyUsageRepository

    private fun onStart() {
        startTime = System.currentTimeMillis()
        job = lifecycleScope.launch(Dispatchers.IO) {
            while(true) {
                val now = System.currentTimeMillis()
                _elapsed.value = (now - startTime) / 1000
                Log.d("UsageCountTimeService", "Time: ${_elapsed.value}")
                delay(1000L)
            }
        }

    }

    private fun onEnd() {
        job?.cancel()
        val endTs = System.currentTimeMillis()
        val userTime = endTs - startTime

        lifecycleScope.launch(Dispatchers.IO) {
            repo.updateTimeInDailyUsage(currentPackage.idHistory, userTime)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
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