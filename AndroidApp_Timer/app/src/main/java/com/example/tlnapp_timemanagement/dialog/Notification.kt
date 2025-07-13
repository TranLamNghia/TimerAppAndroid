package com.example.tlnapp_timemanagement.dialog

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tlnapp_timemanagement.R

object Notification {
    private val CHANNEL_ID = "timer_channel"
    private val NOTIF_ID = 1001

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notifications"
            val descriptionText = "Channel for timer progress notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showSimpleNotification(context: Context, title: String, content: String, notificationId: Int = NOTIF_ID)  {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_smartphone)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun showSimpleNotification_HighPriority(context: Context, title: String, content: String, notificationId: Int = NOTIF_ID)  {
        createNotificationChannel(context) // <-- Đảm bảo gọi trước!
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_smartphone)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)   // <--- Đổi ở đây!
            .setDefaults(NotificationCompat.DEFAULT_ALL)      // <--- Có âm thanh, rung...
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // <--- Gợi ý cho Android rằng đây là thông báo cần chú ý

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun showProgressNotification(context: Context, title: String, content: String, progress: Int, notificationId: Int = NOTIF_ID) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_smartphone)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(100, progress, false)
            .setOnlyAlertOnce(true)

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())

    }
}