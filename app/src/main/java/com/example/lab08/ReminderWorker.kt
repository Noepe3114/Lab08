package com.example.lab08

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = TaskDatabase.getDatabase(applicationContext).taskDao()
        val pending = dao.getPendingCount()
        if (pending > 0) {
            NotificationHelper.showPendingTasks(applicationContext, pending)
        }
        return Result.success()
    }
}