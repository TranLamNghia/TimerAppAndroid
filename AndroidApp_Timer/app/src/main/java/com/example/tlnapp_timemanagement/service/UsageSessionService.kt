package com.example.tlnapp_timemanagement.service

import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import java.time.Duration

class UsageSessionService : Service() {
    companion object {
        const val OBSERVER_ID = 1001
        // Thay list package này bằng danh sách các app bạn muốn theo dõi
        val TRACKED_PACKAGES = listOf("com.google.android.youtube", "com.facebook.katana")
        // Thời gian giới hạn (ví dụ: 10 phút)
        const val TIME_LIMIT_MILLIS: Long = 10 * 60 * 1000
    }


    override fun onCreate() {
        super.onCreate()
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val pi = PendingIntent.getBroadcast(
            this, OBSERVER_ID, Intent(this, UsageSessionReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        usm.registerUsageSessionObserver(
            OBSERVER_ID,
            TRACKED_PACKAGES.toTypedArray(),
            Duration.ofMillis(TIME_LIMIT_MILLIS),
            Duration.ofSeconds(1),
            pi
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        usm.unregisterUsageSessionObserver(OBSERVER_ID)
    }

    override fun onBind(intent: Intent?) = null
}

private fun UsageStatsManager.unregisterUsageSessionObserver(observerId: Int) {

}

private fun UsageStatsManager.registerUsageSessionObserver(
    observerId: Int,
    toTypedArray: Array<String>,
    ofMillis: Duration?,
    ofSeconds: Duration?,
    pi: PendingIntent?
) {

}
