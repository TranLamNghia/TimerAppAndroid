package com.example.tlnapp_timemanagement.main

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButtonToggleGroup
import java.util.*
import java.util.concurrent.TimeUnit

class StatisticsFragment : Fragment() {

    private lateinit var timePeriodToggleGroup: MaterialButtonToggleGroup
    private lateinit var appUsageRecyclerView: RecyclerView
    private lateinit var noDataText: TextView

    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var packageManager: PackageManager

    private var currentFilter : String = "TODAY"

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

        val entries = listOf(
            Entry(0f, 60f),
            Entry(1f, 42f),
            Entry(2f, 16f),
            Entry(3f, 32f),
            Entry(4f, 32f),
            Entry(5f, 60f),
            Entry(6f, 60f),
            Entry(7f, 0f),
            Entry(8f, 32f),
            Entry(9f, 40f)

        )

        val dataSet = LineDataSet(entries, "Sample Data")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(true)
        dataSet.lineWidth = 2f

        val lineData = LineData(dataSet)

        val lineChart = view.findViewById<LineChart>(R.id.line_chart)
        lineChart.setTouchEnabled(true)
        lineChart.setDragEnabled(true)
        lineChart.setScaleEnabled(false)
        lineChart.description.isEnabled = false
        lineChart.setPinchZoom(false)
        lineChart.isDragEnabled = true
        lineChart.data = lineData
        lineChart.animateX(500)
        lineChart.setVisibleXRangeMaximum(5f) // Chỉ hiển thị 5 điểm một lúc
        lineChart.moveViewToX(0f)             // Bắt đầu từ đầu
        lineChart.invalidate()

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        val yAxisRight = lineChart.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.axisMinimum = 0f     // Giá trị thấp nhất của trục Y
        yAxisLeft.axisMaximum = 60f
        yAxisLeft.setLabelCount(6, true)  // 6 nhãn, force fixed spacing
        yAxisLeft.granularity = 10f       // mỗi bước là 10 đơn vị

        lineChart.setBackgroundColor(Color.WHITE)
        lineChart.legend.isEnabled = true
        lineChart.invalidate()
    }

    private fun setupToggleGroup() {
        timePeriodToggleGroup.check(R.id.btn_today) // Select 1 day by default

        timePeriodToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_today -> currentFilter = "TODAY" // Hôm nay
                    R.id.btn_yesterday -> currentFilter = "YESTERDAY" // Hôm qua
                    R.id.btn_this_week -> currentFilter = "THIS_WEEK" // Tuần này
                    R.id.btn_this_month -> currentFilter = "THIS_MONTH" // Approx 1 month
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
        noDataText.text = "NO"
    }

    private fun loadUsageStats() {
//        val endTime = System.currentTimeMillis()
//        val startTime = endTime - currentFilter
//
//        Log.d("loadUsageStats", "EndTime : " + endTime.toString() + "______" + "StartTime : " + startTime.toString() + "_____________" +currentFilter)
//
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        var endTime = System.currentTimeMillis() // 22:30 ngày 21/7/2025
        var startTime: Long

        when (currentFilter) {
            "TODAY" -> { // Hôm nay (00:00:00 ngày 21/7/2025 đến 22:30)
                startTime = calendar.timeInMillis
            }
            "YESTERDAY" -> { // Hôm qua (00:00:00 ngày 20/7/2025 đến 23:59:59)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                startTime = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_MONTH, 1) // Đặt lại
                calendar.add(Calendar.SECOND, -1) // 23:59:59 ngày 20/7/2025
                endTime = calendar.timeInMillis
            }
            "THIS_WEEK" -> { // Tuần này (từ thứ Hai 14/7/2025 đến 22:30 ngày 21/7/2025)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, -1)
                }
                startTime = calendar.timeInMillis
            }
            "THIS_MONTH" -> { // Tháng này (từ 00:00:00 ngày 1/7/2025 đến 22:30 ngày 21/7/2025)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                startTime = calendar.timeInMillis
            }
            else -> {
                startTime = calendar.timeInMillis
            }
        }

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val appUsageMap = mutableMapOf<String, Long>()
        Log.d("loadUsageStats", startTime.toString() + "------------------------------------------------------" + endTime.toString())
        for (usageStats in usageStatsList) {
            val packageName = usageStats.packageName
            val totalTimeInForeground = usageStats.totalTimeVisible
            if (totalTimeInForeground > 0) {
                Log.d("loadUsageStats", "packageName : " + packageName.toString() + "______" + "totalTimeInForeground : " + totalTimeInForeground.toString())
            }

            if (totalTimeInForeground > 0) {
                appUsageMap[packageName] = appUsageMap.getOrDefault(packageName, 0L) + totalTimeInForeground
            }
        }
        Log.d("loadUsageStats", "------------------------------------------------------")

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
//        val hours = TimeUnit.MILLISECONDS.toHours(millis)
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
//        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        val hours = (millis / 1000) / 3600
        val minutes = ((millis / 1000) % 3600) / 60
        val seconds = ((millis / 1000) % 3600) % 60

        return when {
            hours > 0 -> String.format(Locale.getDefault(), "%d h %02d m", hours, minutes)
            minutes > 0 -> String.format(Locale.getDefault(), "%d m %02d s", minutes, seconds)
            else -> String.format(Locale.getDefault(), "%d s", seconds)
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
