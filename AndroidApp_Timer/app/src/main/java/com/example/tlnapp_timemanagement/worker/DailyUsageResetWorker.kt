package com.example.tlnapp_timemanagement.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tlnapp_timemanagement.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyUsageResetWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.dailyUsageDao()
        withContext(Dispatchers.IO) {
            dao.deleteAll()
        }
        return Result.success()
    }


}