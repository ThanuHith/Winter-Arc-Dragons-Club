package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.WinterArcDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Reminder"
        val taskSlot = intent.getStringExtra(EXTRA_TASK_SLOT) ?: ""

        if (taskId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Verify the task still exists and is not already completed
                val db = WinterArcDatabase.getDatabase(context)
                val task = db.winterArcDao().getTaskById(taskId)
                if (task != null && !task.isCompleted) {
                    TaskNotificationHelper.showTaskNotification(
                        context = context,
                        taskId = taskId,
                        taskTitle = task.title,
                        taskSlot = task.slot
                    )
                }
            } catch (e: Exception) {
                // Fallback to displaying notification with intent payload
                TaskNotificationHelper.showTaskNotification(
                    context = context,
                    taskId = taskId,
                    taskTitle = taskTitle,
                    taskSlot = taskSlot
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TASK_REMINDER = "com.example.action.TASK_REMINDER"
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val EXTRA_TASK_TITLE = "EXTRA_TASK_TITLE"
        const val EXTRA_TASK_SLOT = "EXTRA_TASK_SLOT"
    }
}
