package com.example.tlnapp_timemanagement.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.repository.HistoryAppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class FocusDetectService : AccessibilityService() {

    lateinit var repo: HistoryAppRepository
    private var currentTrackedPackage: String? = null

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

        val window = ev.source ?: run {
            Log.d("FDService", "Source is null for package: $pkg")
            return
        }
        val isTopLevel = isTopLevelWindow(window)
        val isSystem = isSystemPackage(pkg)

        // Debug log để kiểm tra điều kiện
        Log.d("FDService", "Package: $pkg, isTopLevel: $isTopLevel, isSystem: $isSystem")

        // Chỉ xử lý nếu là cửa sổ cấp cao nhất và không phải ứng dụng hệ thống
        if (!isTopLevel || isSystem) {
            Log.d("FDService", "Event ignored for package: $pkg (not top-level or system)")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("FDService", "App chuyển foreground: $pkg at $timestamp")
            val currentApp = getCurrentApp()
            if (currentApp == null) return@launch

            if (pkg == currentApp.packageName) {
                currentTrackedPackage = pkg
                Log.d("FDService", "App current : " + currentApp.packageName + " OPEN")
                repo.onAppForeground(currentApp, now)
            } else {
                if (currentTrackedPackage == currentApp.packageName) {
                    Log.d("FDService", "App current : " + currentApp.packageName + " CLOSE")
                    repo.onAppSessionEnd(currentApp, System.currentTimeMillis())
                    currentTrackedPackage = null
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

    /**
     * Kiểm tra xem cửa sổ có phải là cửa sổ cấp cao nhất (top-level) không
     */
    private fun isTopLevelWindow(node: AccessibilityNodeInfo): Boolean {
        return node.isAccessibilityFocused || node.isFocused
    }

    /**
     * Kiểm tra xem gói có phải là ứng dụng hệ thống không
     */
    private fun isSystemPackage(packageName: String): Boolean {
        try {
            val ai = packageManager.getApplicationInfo(packageName, 0)
            return (ai.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            return true
        }
    }
}