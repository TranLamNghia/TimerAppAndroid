package com.example.tlnapp_timemanagement

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.example.tlnapp_timemanagement.dialog.Premium_ProfileFrag
import com.example.tlnapp_timemanagement.main.FAQFrag_ProfileFragment
import com.example.tlnapp_timemanagement.main.FeedbackFrag_ProfileFragment
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var settingsCard: CardView
    private lateinit var accessibilityCard: CardView
    private lateinit var premiumCard: CardView
    private lateinit var faqCard: CardView
    private lateinit var feedbackCard: CardView

    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var languageSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupClickListeners()
        setupLanguageSpinner()
        loadSettings()

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val sharedPref = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val current = sharedPref.getBoolean("dark_mode_enabled", false)
            if (isChecked != current) {
                applyDarkMode(isChecked)
            }
        }
    }

    private fun initViews(view: View) {
        settingsCard = view.findViewById(R.id.settings_card)
        accessibilityCard = view.findViewById(R.id.accessibility_card)
        premiumCard = view.findViewById(R.id.premium_card)
        faqCard = view.findViewById(R.id.faq_card)
        feedbackCard = view.findViewById(R.id.feedback_card)

        darkModeSwitch = view.findViewById(R.id.dark_mode_switch)
        languageSpinner = view.findViewById(R.id.language_spinner)
    }

    private fun setupClickListeners() {
        settingsCard.setOnClickListener {
            toggleSettingsVisibility()
        }

        accessibilityCard.setOnClickListener {
            openAccessibilitySettings()
        }

        premiumCard.setOnClickListener {
            showPremiumDialog()
        }

        faqCard.setOnClickListener {
            openFAQFragment()
        }

        feedbackCard.setOnClickListener {
            openFeedbackFragment()
        }

    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("Tiếng Việt", "English")
        val languageCodes = arrayOf("vi", "en")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Set current selection
        val currentLanguage = loadLanguageSetting()
        val position = languageCodes.indexOf(currentLanguage)
        if (position >= 0) {
            languageSpinner.setSelection(position)
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguageCode = languageCodes[position]
                if (selectedLanguageCode != loadLanguageSetting()) {
                    applyLanguage(selectedLanguageCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun toggleSettingsVisibility() {
        val settingsOptions = view?.findViewById<LinearLayout>(R.id.settings_options)
        val arrowIcon = view?.findViewById<ImageView>(R.id.settings_arrow)

        settingsOptions?.let { options ->
            arrowIcon?.let { arrow ->
                if (options.visibility == View.VISIBLE) {
                    options.visibility = View.GONE
                    arrow.animate().rotation(0f).setDuration(200).start()
                } else {
                    options.visibility = View.VISIBLE
                    arrow.animate().rotation(180f).setDuration(200).start()
                }
            }
        }
    }

    private fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở cài đặt trợ năng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPremiumDialog() {
        val dialog = Premium_ProfileFrag()
        dialog.show(parentFragmentManager, "PremiumDialog")
    }

    private fun openFAQFragment() {
        val faqFragment = FAQFrag_ProfileFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, faqFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openFeedbackFragment() {
        val feedbackFragment = FeedbackFrag_ProfileFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, feedbackFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadSettings() {
        val sharedPref = requireContext().getSharedPreferences("app_settings", 0)

        darkModeSwitch.isChecked = sharedPref.getBoolean("dark_mode_enabled", false)
        val languageCode = sharedPref.getString("language_code", "vi") ?: "vi"
        val position = when (languageCode) {
            "vi" -> 0
            "en" -> 1
            else -> 0
        }
        languageSpinner.setSelection(position)
    }

    private fun saveDarkModeSetting(enabled: Boolean) {
        val sharedPref = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("dark_mode_enabled", enabled)
            apply()
        }

        Toast.makeText(context,
            if (enabled) "Chế độ tối sẽ được bật khi khởi động lại ứng dụng" else "Chế độ sáng sẽ được bật khi khởi động lại ứng dụng",
            Toast.LENGTH_SHORT).show()
    }

    private fun saveLanguageSetting(languageCode: String) {
        val sharedPref = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language_code", languageCode)
            apply()
        }
    }

    private fun loadLanguageSetting(): String {
        val sharedPref = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPref.getString("language_code", "vi") ?: "vi"
    }

    private fun applyDarkMode(enabled: Boolean) {
        // Lưu setting
        saveDarkModeSetting(enabled)

        if (enabled) {
            requireActivity().setTheme(R.style.AppTheme_Dark)
        } else {
            requireActivity().setTheme(R.style.AppTheme)
        }

        requireActivity().recreate()

    }

    private fun applyLanguage(languageCode: String) {
        saveLanguageSetting(languageCode)
        requireActivity().recreate()
    }
}
