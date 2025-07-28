package com.example.tlnapp_timemanagement.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.tlnapp_timemanagement.dialog.Notification
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BroadcastReceiverService : BroadcastReceiver() {

    override fun onReceive(p0: Context, p1: Intent?) {
        val now = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(System.currentTimeMillis())
        Notification.showSimpleNotification(p0, "Reset Usage Stats", "Daily usage reset in: $now")
        scheduleExactReset(p0)
    }

    companion object {
        @SuppressLint("ResetAppIfNewDay")
        fun scheduleExactReset(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = Intent(context, BroadcastReceiverService::class.java)
            val pendingIntent = android.app.PendingIntent.getBroadcast(context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 4)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DATE,1)
            }
            if (ensureExactAlarmPermission(context)) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                // Có thể log / thông báo cho user rằng họ cần bật quyền
            }
        }

        private fun ensureExactAlarmPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val am = context.getSystemService(AlarmManager::class.java)
                if (!am.canScheduleExactAlarms()) {
                    // Chưa có quyền, dẫn user vào Settings để bật
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(it)
                    }
                    return false
                }
            }
            return true
        }
    }




}