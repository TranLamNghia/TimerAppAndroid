package com.example.tlnapp_timemanagement.main

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.*
import java.util.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tlnapp_timemanagement.dialog.AppSetting_TimerFrag
import com.example.tlnapp_timemanagement.R
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.viewmodel.HistoryAppViewModel

class TimerFragment : Fragment(), AppSetting_TimerFrag.OnAppSettingListener {
    private var currentapp = HistoryApp(null)


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
    private var timerRunning = false
    private var timeLeftInMillis: Long = 0
    private var startTimeInMillis: Long = 0

    private var currentAppInfo: ApplicationInfo? = null
    private var currentTimeLimit = 60

    private lateinit var historyAppViewModel: HistoryAppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyAppViewModel = ViewModelProvider(this).get(HistoryAppViewModel::class.java)

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

        // Set initial values
        setDefaultApp()
        updateTimerDisplay()

        // Set click listeners
        btnDelete.setOnClickListener { resetTimer() }
        btnSetup.setOnClickListener { showSetupDialog() }
    }

    private fun setDefaultApp() {
        // Set default app (you can customize this)
        selectedAppName.text = "Chọn ứng dụng"
        selectedAppTimeLimit.text = "Thời gian giới hạn: $currentTimeLimit phút"
        selectedAppIcon.setImageResource(R.drawable.ic_smartphone)

        startTimeInMillis = currentTimeLimit * 60 * 1000L
        timeLeftInMillis = startTimeInMillis
    }

    private fun updateAppInfo(appInfo: ApplicationInfo, timeLimit: Int) {
        currentAppInfo = appInfo
        currentTimeLimit = timeLimit

        val pm = requireContext().packageManager
        try {
            selectedAppName.text = pm.getApplicationLabel(appInfo)
            selectedAppIcon.setImageDrawable(pm.getApplicationIcon(appInfo))
        } catch (e: PackageManager.NameNotFoundException) {
            selectedAppName.text = appInfo.packageName
            selectedAppIcon.setImageResource(R.drawable.ic_smartphone)
        }

        selectedAppTimeLimit.text = "Thời gian giới hạn: $timeLimit phút"

        // Reset timer with new time limit
        startTimeInMillis = timeLimit * 60 * 1000L
        resetTimer()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                timerRunning = false
                timerStatus.text = "Hết thời gian!"
                timeLeftInMillis = 0
                updateTimerDisplay()
            }
        }.start()

        timerRunning = true
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = startTimeInMillis
        updateTimerDisplay()
        timerStatus.text = "Thời gian còn lại"
        timerRunning = false
        if (!app.packageName.equals("NULL")) historyAppViewModel.deleteHistory(app)
    }

    private fun updateTimerDisplay() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeLeftFormatted = if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        timerText.text = timeLeftFormatted

        val progress = (100 - ((timeLeftInMillis * 100) / startTimeInMillis)).toInt()
        timerProgress.progress = progress
    }

    private fun showSetupDialog() {
        val dialog = AppSetting_TimerFrag()
        dialog.setOnAppSettingListener(this)
        dialog.show(parentFragmentManager, "AppSettingDialog")
    }

    override fun onAppSettingSaved(appInfo: ApplicationInfo, timeLimit: Int) {
        val pm = requireContext().packageManager
        if currentapp != null
        val app = HistoryApp(
            idHistory = 0,
            packageName = appInfo.packageName,
            timeLimit = timeLimit,
            beginTime = null,
            endTime = null,
            status = "PENDING"
        )
        historyAppViewModel.insertHistory(app)
        currentapp = app
        updateAppInfo(appInfo, timeLimit)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
