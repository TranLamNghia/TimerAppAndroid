package com.example.tlnapp_timemanagement.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {
    companion object {
        var instance: MyAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    fun redirectToHome() {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

}