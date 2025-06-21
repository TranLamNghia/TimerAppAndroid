package com.example.tlnapp_timemanagement.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.repository.HistoryAppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class FocusDetectService : AccessibilityService() {

    lateinit var repo: HistoryAppRepository


    override fun onServiceConnected() {
        super.onServiceConnected()
        val db = AppDatabase.getDatabase(applicationContext)
        repo = HistoryAppRepository(db.historyAppDao(), db.dailyUsageDao())
        Log.d("FocusDetectService","AccessibilityService connected")
    }


    @SuppressLint("NewApi")
    override fun onAccessibilityEvent(ev: AccessibilityEvent?) {
        if (ev?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = ev.packageName?.toString() ?: return
        val now = Instant.now()
        val timestamp = System.currentTimeMillis()
        Log.d("FDService", "App chuyển foreground: $pkg at $timestamp")

        CoroutineScope(Dispatchers.IO).launch {
            val currentApp = getCurrentApp()
            if (currentApp == null) return@launch
            if (pkg == currentApp.packageName) {
                repo.onAppForeground(currentApp, now)
                repo.onAppSessionEnd(currentApp, System.currentTimeMillis())
            }
        }
    }
    override fun onInterrupt() = Unit

    suspend fun getCurrentApp() : HistoryApp? {
        val pending = repo.getAppByStatus("PENDING")
        val active  = if (pending == null) repo.getAppByStatus("ACTIVE") else null
        return pending ?: active
    }
}