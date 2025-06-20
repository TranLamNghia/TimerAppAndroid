package com.example.tlnapp_timemanagement.service

import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tlnapp_timemanagement.data.AppDatabase
import com.example.tlnapp_timemanagement.data.repository.HistoryAppRepository


class UsageSessionReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        val pkg      = intent.getStringExtra(UsageStatsManager.EXTRA_PACKAGE_NAME)
        val event    = intent.getIntExtra(
            UsageStatsManager.EXTRA_USAGE_SESSION_EVENT_TYPE, -1)
        val timestamp = intent.getLongExtra(
            UsageStatsManager.EXTRA_USAGE_SESSION_EVENT_TIME_MS, System.currentTimeMillis()
        )
        if (pkg == null) return

        val db   = AppDatabase.getDatabase(ctx)
        val repo = HistoryAppRepository(db.historyAppDao(), db.dailyUsageDao())

        CoroutineScope(Dispatchers.IO).launch {
            when (event) {
                UsageStatsManager.USAGE_SESSION_EVENT_CONTINUED -> {
                    // phiên mới bắt đầu
                    repo.onAppSessionStart(pkg, timestamp)
                }
                UsageStatsManager.USAGE_SESSION_EVENT_STOPPED  -> {
                    // phiên kết thúc
                    repo.onAppSessionEnd(pkg, timestamp)
                }
            }
        }
    }
}
