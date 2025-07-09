package com.example.tlnapp_timemanagement.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.repository.HistoryAppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

//Accessibility trigger
class FocusDetectService : AccessibilityService() {

    lateinit var repo: HistoryAppRepository
    private var currentTrackedPackage: String? = null
    private var stopJob: Job? = null
    private var homePackage: String? = null
    private lateinit var userApps: Set<String>
    private var isOpen = false
    private val STOP_DEBOUNCE_MS = 1000L

    override fun onServiceConnected() {
        super.onServiceConnected()
        val db = AppDatabase.getDatabase(applicationContext)
        repo = HistoryAppRepository(db.historyAppDao(), db.dailyUsageDao())

        val pm = packageManager
        userApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter{(it.flags and ApplicationInfo.FLAG_SYSTEM) == 0}
            .map{ it.packageName}
            .toSet()
        Log.d("FDService", "Installed apps: ${userApps.size}")
        homePackage = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }.let { pm.resolveActivity(it, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName }

    }


    @SuppressLint("NewApi")
    override fun onAccessibilityEvent(ev: AccessibilityEvent?) {
        if (ev?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = ev.packageName?.toString() ?: return
        val timestamp = System.currentTimeMillis()


        CoroutineScope(Dispatchers.IO).launch {
            Log.d("FDService", "App chuyển foreground: $pkg at $timestamp")
            val currentApp = getCurrentApp()
            if (currentApp == null) return@launch
            Log.d("FDService", "App current : " + currentApp.packageName + "________" + isOpen)
            if (pkg == currentApp.packageName && isOpen == false) {
                stopJob?.cancel()
                isOpen = true
                currentTrackedPackage = pkg
                Log.d("FDService", "App current : " + currentApp.packageName + " OPEN")
                repo.onAppForeground(currentApp)
                startService(Intent(applicationContext, UsageCountTimeService::class.java).apply {
                    action = "START"
                    putExtra("EXTRA_ID_PACKAGE", currentApp.idHistory)
                })
            } else {
                stopJob?.cancel()
                stopJob = launch {
                    delay(STOP_DEBOUNCE_MS)
//                    if (currentTrackedPackage == currentApp.packageName && pkg == "com.google.android.apps.nexuslauncher" && isOpen == true) {
                    if (currentTrackedPackage == currentApp.packageName && pkg == homePackage && isOpen == true) {
                        isOpen = false
                        Log.d("FDService", "App current : " + currentApp.packageName + " CLOSE")
                        repo.onAppSessionEnd(currentApp)
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