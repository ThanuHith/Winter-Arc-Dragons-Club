package com.example.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TaskNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        val taskTitle = inputData.getString(KEY_TASK_TITLE) ?: "Task Reminder"
        val taskSlot = inputData.getString(KEY_TASK_SLOT) ?: ""

        if (taskId == -1L) return Result.failure()

        TaskNotificationHelper.showTaskNotification(
            context = context,
            taskId = taskId,
            taskTitle = taskTitle,
            taskSlot = taskSlot
        )

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "KEY_TASK_ID"
        const val KEY_TASK_TITLE = "KEY_TASK_TITLE"
        const val KEY_TASK_SLOT = "KEY_TASK_SLOT"
    }
}
