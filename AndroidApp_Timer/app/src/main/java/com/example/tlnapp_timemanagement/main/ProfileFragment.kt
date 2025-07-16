package com.example.tlnapp_timemanagement.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.tlnapp_timemanagement.R
import com.example.tlnapp_timemanagement.dialog.Premium_ProfileFrag

class ProfileFragment : Fragment() {
    private lateinit var settingsCard: CardView
    private lateinit var accessibilityCard: CardView
    private lateinit var premiumCard: CardView
    private lateinit var faqCard: CardView
    private lateinit var feedbackCard: CardView

    private lateinit var darkModeSwitch: Switch
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

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveDarkModeSetting(isChecked)
        }
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("Tiếng Việt", "English")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveLanguageSetting(position)
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
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
        languageSpinner.setSelection(sharedPref.getInt("language_selection", 0))
    }

    private fun saveDarkModeSetting(enabled: Boolean) {
        val sharedPref = requireContext().getSharedPreferences("app_settings", 0)
        with(sharedPref.edit()) {
            putBoolean("dark_mode_enabled", enabled)
            apply()
        }

        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        Toast.makeText(context,
            if (enabled) "Chế độ tối đã được bật" else "Chế độ sáng đã được bật",
            Toast.LENGTH_SHORT).show()
    }

    private fun saveLanguageSetting(position: Int) {
        val sharedPref = requireContext().getSharedPreferences("app_settings", 0)
        with(sharedPref.edit()) {
            putInt("language_selection", position)
            apply()
        }

        val languageName = if (position == 0) "Tiếng Việt" else "English"
    }
}
