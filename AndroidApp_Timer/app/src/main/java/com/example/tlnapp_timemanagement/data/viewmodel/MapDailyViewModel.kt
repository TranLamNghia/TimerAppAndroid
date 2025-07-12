package com.example.tlnapp_timemanagement.data.viewmodel

import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider

class MapDailyViewModel : LifecycleService() {
    private lateinit var sharedUsageViewModel: MapDailyViewModel

    override fun onCreate() {
        super.onCreate()
        sharedUsageViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(MapDailyViewModel::class.java)
    }
}