package com.example.tlnapp_timemanagement.main

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tlnapp_timemanagement.R
import com.google.android.material.button.MaterialButtonToggleGroup
import java.util.*
import java.util.concurrent.TimeUnit

class StatisticsFragment : Fragment() {

    private lateinit var timePeriodToggleGroup: MaterialButtonToggleGroup
    private lateinit var appUsageRecyclerView: RecyclerView
    private lateinit var noDataText: TextView

    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var packageManager: PackageManager

    private var currentFilter = TimeUnit.DAYS.toMillis(1) // Default to 1 day

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usageStatsManager = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        packageManager = requireContext().packageManager

        timePeriodToggleGroup = view.findViewById(R.id.time_period_toggle_group)
        appUsageRecyclerView = view.findViewById(R.id.app_usage_recycler_view)
        noDataText = view.findViewById(R.id.no_data_text)

        setupToggleGroup()
        checkUsageStatsPermission()
    }

    private fun setupToggleGroup() {
        timePeriodToggleGroup.check(R.id.btn_1_day) // Select 1 day by default

        timePeriodToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_1_day -> currentFilter = TimeUnit.DAYS.toMillis(1)
                    R.id.btn_3_days -> currentFilter = TimeUnit.DAYS.toMillis(3)
                    R.id.btn_7_days -> currentFilter = TimeUnit.DAYS.toMillis(7)
                    R.id.btn_1_month -> currentFilter = TimeUnit.DAYS.toMillis(30) // Approx 1 month
                }
                loadUsageStats()
            }
        }
    }

    private fun checkUsageStatsPermission() {
        val appOps = requireContext().getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            requireContext().packageName
        )

        if (mode == android.app.AppOpsManager.MODE_ALLOWED) {
            loadUsageStats()
        } else {
            showPermissionRequest()
        }
    }

    private fun showPermissionRequest() {
        Toast.makeText(context, getString(R.string.accessibility_description), Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
        noDataText.visibility = View.VISIBLE
        noDataText.text = "NO"// Add this string to strings.xml
    }

    private fun loadUsageStats() {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - currentFilter

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, // Or INTERVAL_WEEKLY, INTERVAL_MONTHLY depending on your needs
            startTime,
            endTime
        )

        val appUsageMap = mutableMapOf<String, Long>()
        for (usageStats in usageStatsList) {
            val packageName = usageStats.packageName
            val totalTimeInForeground = usageStats.totalTimeInForeground
            if (totalTimeInForeground > 0) {
                appUsageMap[packageName] = appUsageMap.getOrDefault(packageName, 0L) + totalTimeInForeground
            }
        }

        val sortedAppUsage = appUsageMap.entries
            .filter { entry ->
                try {
                    val appInfo = packageManager.getApplicationInfo(entry.key, PackageManager.GET_META_DATA)
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 // Filter out system apps
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }
            .sortedByDescending { it.value }
            .map { entry ->
                val appInfo = try {
                    packageManager.getApplicationInfo(entry.key, PackageManager.GET_META_DATA)
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
                AppUsageItem(appInfo, entry.value)
            }

        if (sortedAppUsage.isNotEmpty()) {
            appUsageRecyclerView.visibility = View.VISIBLE
            noDataText.visibility = View.GONE
            appUsageRecyclerView.adapter = AppUsageAdapter(sortedAppUsage)
        } else {
            appUsageRecyclerView.visibility = View.GONE
            noDataText.visibility = View.VISIBLE
            noDataText.text = getString(R.string.no_usage_data)
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours > 0 -> String.format(Locale.getDefault(), "%d giờ %02d phút", hours, minutes)
            minutes > 0 -> String.format(Locale.getDefault(), "%d phút %02d giây", minutes, seconds)
            else -> String.format(Locale.getDefault(), "%d giây", seconds)
        }
    }

    data class AppUsageItem(val appInfo: ApplicationInfo?, val usageTimeMillis: Long)

    inner class AppUsageAdapter(private val appUsageList: List<AppUsageItem>) :
        RecyclerView.Adapter<AppUsageAdapter.AppUsageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppUsageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_usage, parent, false)
            return AppUsageViewHolder(view)
        }

        override fun onBindViewHolder(holder: AppUsageViewHolder, position: Int) {
            val item = appUsageList[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int = appUsageList.size

        inner class AppUsageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val appIcon: ImageView = itemView.findViewById(R.id.app_icon)
            private val appName: TextView = itemView.findViewById(R.id.app_name)
            private val appUsageTime: TextView = itemView.findViewById(R.id.app_usage_time)
            private val usageProgressBar: ProgressBar = itemView.findViewById(R.id.usage_progress_bar)

            fun bind(item: AppUsageItem) {
                if (item.appInfo != null) {
                    appIcon.setImageDrawable(packageManager.getApplicationIcon(item.appInfo))
                    appName.text = packageManager.getApplicationLabel(item.appInfo)
                } else {
                    appIcon.setImageResource(R.drawable.ic_smartphone) // Default icon
                    appName.text = "Unknown App"
                }
                appUsageTime.text = formatDuration(item.usageTimeMillis)

                // Calculate progress (e.g., relative to the highest usage app or a fixed max)
                val maxUsage = appUsageList.firstOrNull()?.usageTimeMillis ?: 1L
                val progress = if (maxUsage > 0) ((item.usageTimeMillis.toFloat() / maxUsage) * 100).toInt() else 0
                usageProgressBar.progress = progress
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload stats when fragment is resumed (e.g., after granting permission)
        checkUsageStatsPermission()
    }
}
