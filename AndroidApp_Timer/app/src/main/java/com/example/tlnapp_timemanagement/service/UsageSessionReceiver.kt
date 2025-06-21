package com.example.tlnapp_timemanagement.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UsageSessionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pkg = intent.getStringExtra(Companion.EXTRA_OBSERVER_PACKAGE_NAME)
        val eventType = intent.getIntExtra(
            Companion.EXTRA_OBSERVER_EVENT_TYPE, -1)
        when (eventType) {
            USAGE_OBSERVER_EVENT_STARTED -> {

            }
            USAGE_OBSERVER_EVENT_STOPPED -> {

            }
            USAGE_OBSERVER_EVENT_TIME_LIMIT_REACHED -> {

            }
        }
    }

    companion object {
        const val EXTRA_OBSERVER_PACKAGE_NAME = "android.app.usage.extra.OBSERVER_PACKAGE_NAME"
        const val EXTRA_OBSERVER_EVENT_TYPE = "android.app.usage.extra.OBSERVER_EVENT_TYPE"
        const val USAGE_OBSERVER_EVENT_STARTED = 1
        const val USAGE_OBSERVER_EVENT_STOPPED = 2
        const val USAGE_OBSERVER_EVENT_TIME_LIMIT_REACHED = 3
    }
}