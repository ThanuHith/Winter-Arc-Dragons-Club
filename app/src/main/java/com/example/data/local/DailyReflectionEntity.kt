package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reflections")
data class DailyReflectionEntity(
    @PrimaryKey
    val dateKey: String, // "YYYY-MM-DD"
    val wentWell: String = "",
    val distracted: String = "",
    val improveTomorrow: String = "",
    val proudOf: String = "",
    val mood: String = "" // "EXCELLENT", "GOOD", "AVERAGE", "DIFFICULT", ""
)
