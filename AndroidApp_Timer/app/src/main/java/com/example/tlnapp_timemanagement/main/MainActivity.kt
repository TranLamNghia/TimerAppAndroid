package com.example.tlnapp_timemanagement.main

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.res.Configuration
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tlnapp_timemanagement.ProfileFragment
import com.example.tlnapp_timemanagement.R
import com.example.tlnapp_timemanagement.dialog.Notification
import com.example.tlnapp_timemanagement.service.FocusDetectService
import com.example.tlnapp_timemanagement.worker.DailyUsageResetWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val CHANNEL_ID = "timer_channel"
    private var selectedTabId: Int = R.id.nav_timer

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Notification.showSimpleNotification(this, "Đã cấp quyền!", "Bạn đã cấp quyền nhận thông báo")
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showSettingsRedirectDialog()
                } else {
                    Toast.makeText(
                        this,
                        "Quyền thông báo bị từ chối",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        loadAndApplySettings()
        applySavedLanguage()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Notification
        createNotificationChannel()
        checkNotificationPermissionAndSend()

        // AccessibilityService
        if (!isAccessibilityServiceEnabled(this, FocusDetectService::class.java)) {
//            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        // UsageStats
        if (!hasUsageStatsPermission(this)) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//            this.startActivity(intent)
        }

        // Initialize bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        // Schedule daily usage reset
        scheduleDailyUsageResetWork(this)

        // Set default fragment
        if (savedInstanceState != null) {
            selectedTabId = savedInstanceState.getInt("SELECTED_TAB_ID", R.id.nav_timer)
            bottomNavigationView.selectedItemId = selectedTabId
        } else {
            bottomNavigationView.selectedItemId = R.id.nav_timer
            loadFragment(TimerFragment())
        }
    }

    private fun loadAndApplySettings() {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE)

        // Load dark mode setting
        val isDarkMode = sharedPref.getBoolean("dark_mode_enabled", false)
        if (isDarkMode) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme)
        }

        // Load language setting
        val languageCode = sharedPref.getString("language_code", "vi") ?: "vi"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (selectedTabId == item.itemId) return false
        selectedTabId = item.itemId
        var fragment: Fragment? = null

        when (item.itemId) {
            R.id.nav_timer -> fragment = TimerFragment()
            R.id.nav_practice -> fragment = PracticeFragment()
            R.id.nav_profile -> fragment = ProfileFragment()
        }

        return loadFragment(fragment)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notifications"
            val descriptionText = "Channel for timer progress notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val nm = this.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermissionAndSend() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {

                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showPermissionRationaleDialog()
                }
                else -> {
                    Log.d("MessageTest","Denied Permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {

        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cần quyền Thông báo")
            .setMessage("Ứng dụng cần quyền gửi thông báo để hiển thị tiến độ sử dụng.")
            .setPositiveButton("Đồng ý") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }

    private fun showSettingsRedirectDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cho phép Thông báo")
            .setMessage("Bạn đã chặn quyền thông báo. Vui lòng bật lại trong Cài đặt.")
            .setPositiveButton("Mở Cài đặt") { _: DialogInterface, _: Int ->
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, applicationContext.packageName)
                }
                startActivity(intent)
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
        val expectedComponent = ComponentName(context, service)
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = enabledServicesSetting.split(':')
        return colonSplitter.any { it.equals(expectedComponent.flattenToString(), ignoreCase = true) }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
            val mode = appOps.checkOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(),
                context.packageName
            )
            return mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            return false
        }
    }

    private fun scheduleDailyUsageResetWork(context: Context) {
        // Delay = 4AM next day
        val now = Calendar.getInstance()
        val target = now.clone() as Calendar
        target.set(Calendar.HOUR_OF_DAY, 4)
        target.set(Calendar.MINUTE, 0)
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)
        if (target.before(now)) {
            target.add(Calendar.DATE, 1)
        }
        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<DailyUsageResetWorker>(1, java.util.concurrent.TimeUnit.DAYS)
            .setInitialDelay(initialDelay, java.util.concurrent.TimeUnit.MILLISECONDS)
            .addTag("DAILY_USAGE_RESET")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyUsageResetWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelScheduleDaily(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("DailyUsageResetWork")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SELECTED_TAB_ID", selectedTabId)
    }


    private fun applySavedLanguage() {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val languageCode = sharedPref.getString("language_code", "vi") ?: "vi"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}