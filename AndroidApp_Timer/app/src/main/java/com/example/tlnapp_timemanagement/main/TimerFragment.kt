package com.example.tlnapp_timemanagement.main

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import java.util.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tlnapp_timemanagement.dialog.AppSetting_TimerFrag
import com.example.tlnapp_timemanagement.R
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.viewmodel.DailyUsageViewModel
import com.example.tlnapp_timemanagement.data.viewmodel.HistoryAppViewModel
import com.example.tlnapp_timemanagement.dialog.LoadingFrag
import kotlinx.coroutines.*


class TimerFragment : Fragment(), AppSetting_TimerFrag.OnAppSettingListener {
    private var currentapp = HistoryApp(
        idHistory = 0,
        packageName = "Chọn ứng dụng",
        beginTime = null,
        endTime = null,
        timeLimit = 0,
        status = "PENDING"
    )


    private lateinit var timerText: TextView
    private lateinit var timerStatus: TextView
    private lateinit var timerProgress: ProgressBar
    private lateinit var selectedAppName: TextView
    private lateinit var selectedAppTimeLimit: TextView
    private lateinit var selectedAppIcon: ImageView
    private lateinit var selectedAppIconContainer: CardView
    private lateinit var btnDelete: Button
    private lateinit var btnSetup: Button

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var startTimeInMillis: Long = 1

    private lateinit var historyAppViewModel: HistoryAppViewModel
    private lateinit var dailyUsageViewModel: DailyUsageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycleScope.launch {
            showLoadingAndNavigate()
        }
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        historyAppViewModel = ViewModelProvider(this).get(HistoryAppViewModel::class.java)
        dailyUsageViewModel = ViewModelProvider(this).get(DailyUsageViewModel::class.java)


        // Initialize views
        timerText = view.findViewById(R.id.timer_text)
        timerStatus = view.findViewById(R.id.timer_status)
        timerProgress = view.findViewById(R.id.timer_progress)
        selectedAppName = view.findViewById(R.id.selected_app_name)
        selectedAppTimeLimit = view.findViewById(R.id.selected_app_time_limit)
        selectedAppIcon = view.findViewById(R.id.selected_app_icon)
        selectedAppIconContainer = view.findViewById(R.id.selected_app_icon_container)
        btnDelete = view.findViewById(R.id.btn_delete)
        btnSetup = view.findViewById(R.id.btn_setup)

        getCurrentApp()

        btnDelete.setOnClickListener { resetTimer() }
        btnSetup.setOnClickListener { showSetupDialog() }
    }

    fun getCurrentApp() {
        historyAppViewModel.getPendingApp{ beforeApp ->
            if (beforeApp != null) {
                currentapp = beforeApp
                updateAppInfo(currentapp)
            } else {
                historyAppViewModel.getActiveApp { beforeApp ->
                    if (beforeApp != null) {
                        currentapp = beforeApp
                    }
                    updateAppInfo(currentapp)
                }
            }
        }
    }

    fun showLoadingAndNavigate() {
        val loadingDialog = LoadingFrag()
        loadingDialog.show(parentFragmentManager, "loading")

        Handler(Looper.getMainLooper()).postDelayed({
            loadingDialog.dismiss()
        }, 1000)
    }

    private fun setDefaultApp() {
        selectedAppName.text = "Chọn ứng dụng"
        selectedAppTimeLimit.text = "Thời gian giới hạn: -- phút"
        selectedAppIcon.setImageResource(R.drawable.ic_smartphone)
        timeLeftInMillis = 0
        updateTimerDisplay()
    }

    private fun updateAppInfo(currentapp : HistoryApp) {
        val pm = requireContext().packageManager
        if (currentapp.packageName == "Chọn ứng dụng" || currentapp.packageName.isBlank()) {
            selectedAppName.text = "Chọn ứng dụng"
            selectedAppIcon.setImageResource(R.drawable.ic_smartphone)
            selectedAppTimeLimit.text = "Thời gian giới hạn: -- phút"
        } else {
            try {
                val appInfo = pm.getApplicationInfo(currentapp.packageName, 0)
                selectedAppName.text = appInfo.loadLabel(pm)
                selectedAppIcon.setImageDrawable(pm.getApplicationIcon(appInfo))
                selectedAppTimeLimit.text = "Thời gian giới hạn: ${currentapp.timeLimit} phút"

            } catch (e: PackageManager.NameNotFoundException) {
                selectedAppName.text = currentapp.packageName
                selectedAppIcon.setImageResource(R.drawable.ic_smartphone)
                selectedAppTimeLimit.text = "Thời gian giới hạn: ${currentapp.timeLimit} phút"
            }
        }
        startTimeInMillis = currentapp.timeLimit * 60 * 1000L
        dailyUsageViewModel.getDailyUsageTime(currentapp.idHistory).observe(viewLifecycleOwner) { result ->
            timeLeftInMillis = if (result != null) startTimeInMillis - (result * 1000) else startTimeInMillis
            updateTimerDisplay()
        }
    }

    private fun resetTimer() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (currentapp.status.equals("PENDING")) historyAppViewModel.deleteHistoryApp("PENDING")
            else if (currentapp.status.equals("ACTIVE")) historyAppViewModel.updateNewStatusByIdHistory(currentapp.idHistory, "INACTIVE")
        }
        currentapp = HistoryApp(
            idHistory = 0,
            packageName = "Chọn ứng dụng",
            beginTime = null,
            endTime = null,
            timeLimit = 0,
            status = "PENDING"
        )
        setDefaultApp()
    }

    private fun updateTimerDisplay() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = ((timeLeftInMillis / 1000) % 3600) % 60
        val timeLeftFormatted = if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
        timerText.text = timeLeftFormatted
        var progress : Int = 0
        if (startTimeInMillis.toInt() == 0) {
            progress = (100 - ((timeLeftInMillis * 100) / 1)).toInt()
        }
        else {
            progress = (100 - ((timeLeftInMillis * 100) / startTimeInMillis)).toInt()
        }

        timerProgress.progress = progress
    }

    private fun showSetupDialog() {
        val dialog = AppSetting_TimerFrag()
        dialog.setOnAppSettingListener(this)
        dialog.show(parentFragmentManager, "AppSettingDialog")
    }

    override fun onAppSettingSaved(appInfo: ApplicationInfo, timeLimit: Int) {
        val app = HistoryApp(
            idHistory = 0,
            packageName = appInfo.packageName,
            timeLimit = timeLimit,
            beginTime = null,
            endTime = null,
            status = "PENDING"
        )
        viewLifecycleOwner.lifecycleScope.launch {
            val beforeIdAppDaily = dailyUsageViewModel.getIdHistoryEXISTS(app.packageName)
            if (beforeIdAppDaily != null) {
                if (currentapp.status.equals("ACTIVE")) {
                    historyAppViewModel.updateNewStatusByIdHistory(currentapp.idHistory, "INACTIVE")
                } else if (currentapp.status.equals("PENDING")) {
                    historyAppViewModel.deleteHistoryApp("PENDING")
                }
                historyAppViewModel.updateNewStatusByIdHistory(beforeIdAppDaily, "ACTIVE")
                currentapp = historyAppViewModel.getHistoryById(beforeIdAppDaily)
                updateAppInfo(currentapp)
            } else {
                if (currentapp.packageName.equals("Chọn ứng dụng")) {
                    historyAppViewModel.insertHistory(app)
                }
                else if (currentapp.status.equals("PENDING")) {
                    historyAppViewModel.updateApp(currentapp.idHistory, app.packageName, app.timeLimit)
                }
                else {
                    historyAppViewModel.insertHistory(app)
                }
                historyAppViewModel.getAppByMaxIdLive().observe(viewLifecycleOwner) { updatedApp ->
                    if (updatedApp != null) {
                        currentapp = updatedApp
                        updateAppInfo(currentapp)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
