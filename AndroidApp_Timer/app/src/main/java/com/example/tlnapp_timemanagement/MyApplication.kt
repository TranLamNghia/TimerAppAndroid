package com.example.tlnapp_timemanagement

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.tlnapp_timemanagement.data.viewmodel.MapDailyShareViewModel

class MyApplication : Application() {
    val viewModelStore = ViewModelStore()
    val mapDailyShareViewModel: MapDailyShareViewModel by lazy {
        ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory())
            .get(MapDailyShareViewModel::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
    }
}