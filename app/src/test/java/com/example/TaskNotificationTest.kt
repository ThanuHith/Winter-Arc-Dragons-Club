package com.example

import com.example.data.local.TaskEntity
import com.example.data.local.TimeSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class TaskNotificationTest {

    @Test
    fun testTaskEntity_withScheduledTimeMillis() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }
        val task = TaskEntity(
            id = 42L,
            dateKey = "2026-10-02",
            title = "Morning Dragon Run",
            slot = TimeSlot.MORNING.name,
            scheduledTimeMillis = cal.timeInMillis
        )

        assertNotNull(task.scheduledTimeMillis)
        assertEquals(42L, task.id)
        val formatted = task.getFormattedTime()
        assertTrue(formatted.contains("8:30"))
        assertTrue(formatted.contains("AM"))
    }

    @Test
    fun testTaskEntity_fallbackTimeString() {
        val task = TaskEntity(
            id = 1L,
            dateKey = "2026-10-02",
            title = "Night Reflection",
            slot = TimeSlot.NIGHT.name,
            scheduledTime = "21:45"
        )

        val formatted = task.getFormattedTime()
        assertTrue(formatted.contains("9:45"))
        assertTrue(formatted.contains("PM"))
    }
}
