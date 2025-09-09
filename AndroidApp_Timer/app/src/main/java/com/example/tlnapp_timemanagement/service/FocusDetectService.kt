package com.example.tlnapp_timemanagement.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.repository.HistoryAppRepository
import com.example.tlnapp_timemanagement.dialog.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//Accessibility trigger
class FocusDetectService : AccessibilityService() {

    lateinit var repo: HistoryAppRepository
    private var currentTrackedPackage: String? = null
    private var stopJob: Job? = null
    private var homePackage: String? = null
    private lateinit var userApps: Set<String>
    private var isOpen = false
    private val STOP_DEBOUNCE_MS = 500L


    companion object {
        var instance: FocusDetectService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        val db = AppDatabase.getDatabase(applicationContext)
        repo = HistoryAppRepository(db.historyAppDao(), db.dailyUsageDao())

        val pm = packageManager
        userApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter{(it.flags and ApplicationInfo.FLAG_SYSTEM) == 0}
            .map{ it.packageName}
            .toSet()
        homePackage = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }.let { pm.resolveActivity(it, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName }


    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun redirectToHome() {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }


    @SuppressLint("NewApi")
    override fun onAccessibilityEvent(ev: AccessibilityEvent?) {
        if (ev?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = ev.packageName?.toString() ?: return

        CoroutineScope(Dispatchers.Main).launch {
            val currentApp = getCurrentApp()
            if (currentApp == null) return@launch
            if (pkg == currentApp.packageName && isOpen == false) {
                stopJob?.cancel()
                isOpen = true
                currentTrackedPackage = pkg
                repo.onAppForeground(currentApp)
                Notification.showSimpleNotification(applicationContext, "OPEN", "Tracker app is open")
                startService(Intent(applicationContext, UsageCountTimeService::class.java).apply {
                    action = "START"
                    putExtra("EXTRA_ID_PACKAGE", currentApp.idHistory)
                })
            } else {
                stopJob?.cancel()
                stopJob = launch {
                    delay(STOP_DEBOUNCE_MS)
                        if (currentTrackedPackage == currentApp.packageName && pkg == homePackage && isOpen == true) {
                        isOpen = false
                        repo.onAppSessionEnd(currentApp)
                        Notification.showSimpleNotification(applicationContext, "CLOSE", "Tracker app is close")
                        startService(Intent(applicationContext, UsageCountTimeService::class.java).apply {
                            action = "END"
                            putExtra("EXTRA_ID_PACKAGE", currentApp.idHistory)
                        })
                        currentTrackedPackage = null
                    }

                }
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