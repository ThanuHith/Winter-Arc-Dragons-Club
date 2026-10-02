package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TimeSlot(val displayName: String, val icon: String) {
    MORNING("Morning", "🌅"),
    AFTERNOON("Afternoon", "☀️"),
    EVENING("Evening", "🌆"),
    NIGHT("Night", "🌙");

    companion object {
        fun fromString(value: String): TimeSlot {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MORNING
        }
    }
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String, // "YYYY-MM-DD"
    val title: String,
    val slot: String, // "MORNING", "AFTERNOON", "EVENING", "NIGHT"
    val isCompleted: Boolean = false,
    val isTop5: Boolean = false,
    val top5Index: Int = -1, // 0..4
    val scheduledTime: String = "", // "HH:mm" 24h format (e.g. "07:30", "18:00")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        if (scheduledTime.isBlank()) return ""
        return try {
            val parts = scheduledTime.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format("%d:%02d %s", displayHour, minute, amPm)
        } catch (e: Exception) {
            scheduledTime
        }
    }
}
