package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.WinterArcDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d("TaskBootReceiver", "Device reboot detected: re-registering pending task reminders")
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = WinterArcDatabase.getDatabase(context)
                    val now = System.currentTimeMillis()
                    val pendingTasks = db.winterArcDao().getPendingReminderTasks(now)
                    Log.d("TaskBootReceiver", "Found ${pendingTasks.size} pending tasks to re-schedule")
                    for (task in pendingTasks) {
                        TaskReminderScheduler.scheduleReminder(context, task)
                    }
                } catch (e: Exception) {
                    Log.e("TaskBootReceiver", "Failed to reschedule task alarms after boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
