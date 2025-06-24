package com.example.tlnapp_timemanagement.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tlnapp_timemanagement.R

class PracticeFragment : Fragment() {

    private val CHANNEL_ID = "timer_channel"
    private val NOTIF_ID = 1001

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                sendTestProgressNotification()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // User ticked "Don't ask again" hoặc permission đã bị block
                    showSettingsRedirectDialog()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Quyền thông báo bị từ chối",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_practice_placeholder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNotificationChannel()
        checkNotificationPermissionAndSend()
    }

    /** Tạo Notification Channel cho Android 8+ */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notifications"
            val descriptionText = "Channel for timer progress notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val nm = requireContext().getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    /** Kiểm tra permission rồi gửi notification */
    private fun checkNotificationPermissionAndSend() {
        // Nếu Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Đã có quyền
                    sendTestProgressNotification()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Nên hiển thị rationale trước khi request
                    showPermissionRationaleDialog()
                }
                else -> {
                    // Lần đầu tiên hoặc đã block
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Trên Android <13 thì ko cần runtime permission
            sendTestProgressNotification()
        }
    }

    /** Hiển thị dialog giải thích vì sao cần quyền */
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cần quyền Thông báo")
            .setMessage("Ứng dụng cần quyền gửi thông báo để hiển thị tiến độ sử dụng.")
            .setPositiveButton("Đồng ý") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }

    /** Nếu user tick 'Don't ask again' → dẫn vào Settings */
    private fun showSettingsRedirectDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cho phép Thông báo")
            .setMessage("Bạn đã chặn quyền thông báo. Vui lòng bật lại trong Cài đặt.")
            .setPositiveButton("Mở Cài đặt") { _: DialogInterface, _: Int ->
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
                startActivity(intent)
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }

    /** Gửi notification kèm progress 75% */
    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    private fun sendTestProgressNotification() {
        // Trước hết kiểm tra user đã bật notification cho app chưa
        if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            Toast.makeText(
                requireContext(),
                "Thông báo đang bị tắt. Vui lòng bật lại.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val max = 100
        val progress = 75

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_smartphone)
            .setContentTitle("App Timer")
            .setContentText("Đây là đoạn thông báo test")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(max, progress, false)
            .setOnlyAlertOnce(true)

        NotificationManagerCompat.from(requireContext())
            .notify(NOTIF_ID, builder.build())
    }
}
