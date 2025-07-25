package com.example.tlnapp_timemanagement.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.tlnapp_timemanagement.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppSetting_TimerFrag : DialogFragment() {

    private lateinit var editTextAppName: EditText
    private lateinit var spinnerApps: Spinner
    private lateinit var spinnerTimerLimits: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnClose: Button

    private val timeOptions = listOf(2, 15, 30, 45, 60, 90, 120, 180)
    private var filteredApps: List<ApplicationInfo> = emptyList()

    private var allApps: List<ApplicationInfo> = emptyList()

    // Interface to callback about TimerFragment
    interface OnAppSettingListener {
        fun onAppSettingSaved(appInfo: ApplicationInfo, timeLimit: Int)
    }

    private var listener: OnAppSettingListener? = null

    public fun setOnAppSettingListener(listener : OnAppSettingListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_app_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        editTextAppName = view.findViewById(R.id.app_edittext)
        spinnerApps = view.findViewById(R.id.app_spinner)
        spinnerTimerLimits = view.findViewById(R.id.time_limit_spinner)
        btnSave = view.findViewById(R.id.btn_save)
        btnClose = view.findViewById(R.id.btn_cancel)

        initTimeLimits()
        loadAllApps()
        setupAppNameWatcher()
        setupButtons()
    }

    private fun loadAllApps() {
        CoroutineScope(Dispatchers.IO).launch {
            val pm = requireContext().packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfoList = pm.queryIntentActivities(intent, 0)
            allApps = resolveInfoList
                .map { it.activityInfo.applicationInfo }
                .distinctBy { it.packageName }
                .filter{pm.getApplicationLabel(it).toString() != "App Timer"}
                .sortedBy { pm.getApplicationLabel(it).toString() }
            withContext(Dispatchers.Main) {
                updateAppSpinner(allApps)
            }
        }
    }

    private fun setupAppNameWatcher() {
        var searchHandler = Handler(Looper.getMainLooper())
        var searchRunnable: Runnable? = null
        editTextAppName.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().trim()
                    searchRunnable?.let { searchHandler.removeCallbacks(it) }
                    searchRunnable = Runnable {
                        if (input.isEmpty()) {
                            updateAppSpinner(allApps)
                        } else {
                            val filtered = allApps.filter {
                                requireContext().packageManager.getApplicationLabel(it).toString().contains(input, ignoreCase = true)
                            }
                            updateAppSpinner(filtered)
                        }
                    }
                    searchHandler.postDelayed(searchRunnable!!, 300)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            }
        )
    }

    private fun initTimeLimits () {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimerLimits.adapter = adapter
        spinnerTimerLimits.setSelection(3)
    }

    private fun createAppView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        pm: PackageManager
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_app_spinner, parent, false)
        val iconView = view.findViewById<ImageView>(R.id.app_icon)
        val nameView = view.findViewById<TextView>(R.id.app_name)
        val appInfo = spinnerApps.adapter.getItem(position) as ApplicationInfo
        iconView.setImageDrawable(pm.getApplicationIcon(appInfo))
        nameView.text = pm.getApplicationLabel(appInfo)
        return view
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAppsByName(input: String): List<ApplicationInfo> {
        val pm = requireContext().packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfoList = pm.queryIntentActivities(intent, 0)
        return resolveInfoList
            .map { it.activityInfo.applicationInfo }
            .distinctBy { it.packageName }
            .filter { pm.getApplicationLabel(it).toString().contains(input, ignoreCase = true) }
            .sortedBy { pm.getApplicationLabel(it).toString() }
    }

    private fun updateAppSpinner(apps: List<ApplicationInfo>) {
        val pm = requireContext().packageManager
        filteredApps = apps
        val adapter = object : ArrayAdapter<ApplicationInfo>(requireContext(), 0, filteredApps) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createAppView(position, convertView, parent, pm)
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createAppView(position, convertView, parent, pm)
            }
        }
        spinnerApps.adapter = adapter
    }

    private fun setupButtons() {
        btnClose.setOnClickListener {
            dismiss()
        }

        btnSave.setOnClickListener {
            if (spinnerApps.selectedItemPosition >= 0) {
                val selectedApp = filteredApps[spinnerApps.selectedItemPosition]
                val selectedTimeLimit = timeOptions[spinnerTimerLimits.selectedItemPosition]
                listener?.onAppSettingSaved(selectedApp, selectedTimeLimit)
                dismiss()
            } else {
                Toast.makeText(context, "Vui lòng chọn ứng dụng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
