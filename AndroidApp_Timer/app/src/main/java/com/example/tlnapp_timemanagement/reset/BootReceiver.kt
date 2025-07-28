// BootReceiver.kt
package com.example.tlnapp_timemanagement.reset

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tlnapp_timemanagement.service.BroadcastReceiverService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            BroadcastReceiverService.scheduleExactReset(ctx)
        }
    }
}
