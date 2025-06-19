package com.example.tlnapp_timemanagement.main

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.*
import java.util.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tlnapp_timemanagement.dialog.AppSetting_TimerFrag
import com.example.tlnapp_timemanagement.R
import com.example.tlnapp_timemanagement.data.model.HistoryApp
import com.example.tlnapp_timemanagement.data.viewmodel.HistoryAppViewModel

class TimerFragment : Fragment(), AppSetting_TimerFrag.OnAppSettingListener {
    private var currentapp = HistoryApp(
        idHistory = 0,
        packageName = "Chọn ứng dụng",
        beginTime = null,
        endTime = null,
        timeLimit = 60,
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
    private var timerRunning = false
    private var timeLeftInMillis: Long = 25
    private var startTimeInMillis: Long = 123

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

        historyAppViewModel.getPendingApp{ beforeApp ->
            if (beforeApp != null) {
                currentapp = beforeApp
            } else {
                currentapp = HistoryApp(
                    idHistory = 0,
                    packageName = "Chọn ứng dụng",
                    beginTime = null,
                    endTime = null,
                    timeLimit = 60,
                    status = "PENDING"
                )
            }
//            setDefaultApp()
            updateAppInfo(currentapp)
            updateTimerDisplay()
        }

        // Set click listeners
        btnDelete.setOnClickListener { resetTimer() }
        btnSetup.setOnClickListener { showSetupDialog() }

    }

//    private fun setDefaultApp() {
//        val pm = requireContext().packageManager
//        selectedAppName.text = pm.getApplicationInfo(currentapp.packageName, 0).loadLabel(pm)
//        selectedAppTimeLimit.text = currentapp.timeLimit.toString()
//        val icon = try {
//            requireContext().packageManager.getApplicationIcon(currentapp.packageName)
//        } catch (e: PackageManager.NameNotFoundException) {
//            ContextCompat.getDrawable(requireContext(), R.drawable.ic_smartphone)
//        }
//        selectedAppIcon.setImageDrawable(icon)
//        startTimeInMillis = currentTimeLimit * 60 * 1000L
//        timeLeftInMillis = startTimeInMillis
//    }

    private fun updateAppInfo(currentapp : HistoryApp) {
        val pm = requireContext().packageManager
        selectedAppName.text = pm.getApplicationInfo(currentapp.packageName, 0).loadLabel(pm)
        selectedAppTimeLimit.text = "Thời gian giới hạn: ${currentapp.timeLimit} phút"
        val icon = try {
            requireContext().packageManager.getApplicationIcon(currentapp.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_smartphone)
        }
        selectedAppIcon.setImageDrawable(icon)
        startTimeInMillis = currentapp.timeLimit * 60 * 1000L
        timeLeftInMillis = 123
//        resetTimer()
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
        if (!currentapp.packageName.equals("NULL")) historyAppViewModel.deleteHistory(currentapp)
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
        val app = HistoryApp(
            idHistory = 0,
            packageName = appInfo.packageName,
            timeLimit = timeLimit,
            beginTime = null,
            endTime = null,
            status = "PENDING"
        )
        if (currentapp.packageName.equals("NULL")) {
            historyAppViewModel.insertHistory(app)
        }
        else if (currentapp.status.equals("PENDING")) {
            historyAppViewModel.updatePendingApp(app.packageName, app.timeLimit)
        }
        else {
            historyAppViewModel.insertHistory(app)
        }
        currentapp = app
        updateAppInfo(currentapp)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
