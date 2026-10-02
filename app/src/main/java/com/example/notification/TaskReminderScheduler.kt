package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.local.TaskEntity
import java.util.concurrent.TimeUnit

object TaskReminderScheduler {

    private const val TAG = "TaskReminderScheduler"

    fun scheduleReminder(context: Context, task: TaskEntity) {
        val targetMillis = task.scheduledTimeMillis ?: return
        val currentMillis = System.currentTimeMillis()

        if (targetMillis <= currentMillis || task.isCompleted) {
            cancelReminder(context, task.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            action = TaskAlarmReceiver.ACTION_TASK_REMINDER
            putExtra(TaskAlarmReceiver.EXTRA_TASK_ID, task.id)
            putExtra(TaskAlarmReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(TaskAlarmReceiver.EXTRA_TASK_SLOT, task.slot)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                } else {
                    // Fallback to standard inexact alarm while idle
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled exact alarm for task ${task.id} at $targetMillis")
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission denied, falling back to WorkManager", e)
            scheduleWorkManagerFallback(context, task, targetMillis - currentMillis)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm for task ${task.id}", e)
        }
    }

    private fun scheduleWorkManagerFallback(context: Context, task: TaskEntity, delayMillis: Long) {
        val workData = Data.Builder()
            .putLong(TaskNotificationWorker.KEY_TASK_ID, task.id)
            .putString(TaskNotificationWorker.KEY_TASK_TITLE, task.title)
            .putString(TaskNotificationWorker.KEY_TASK_SLOT, task.slot)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<TaskNotificationWorker>()
            .setInputData(workData)
            .setInitialDelay(delayMillis.coerceAtLeast(0L), TimeUnit.MILLISECONDS)
            .addTag("task_${task.id}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun cancelReminder(context: Context, taskId: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
                action = TaskAlarmReceiver.ACTION_TASK_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.toInt(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
            WorkManager.getInstance(context).cancelAllWorkByTag("task_$taskId")
            Log.d(TAG, "Cancelled reminder for task $taskId")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling reminder for task $taskId", e)
        }
    }
}
