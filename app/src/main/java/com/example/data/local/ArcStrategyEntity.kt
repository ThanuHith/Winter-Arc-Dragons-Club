package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "arc_strategy")
data class ArcStrategyEntity(
    @PrimaryKey
    val id: Int = 1,
    val startDate: String = "", // e.g. "2026-10-01"
    val endDate: String = "",   // e.g. "2026-12-30"
    val mainGoal: String = "",
    val focusTagsCsv: String = "", // comma-separated e.g. "Fitness,Learning,Career"
    val milestoneDay1Improve: String = "",
    val milestoneDay1Happened: String = "",
    val milestoneDay1Lessons: String = "",
    val milestoneDay30Improve: String = "",
    val milestoneDay30Happened: String = "",
    val milestoneDay30Lessons: String = "",
    val milestoneDay60Improve: String = "",
    val milestoneDay60Happened: String = "",
    val milestoneDay60Lessons: String = "",
    val milestoneDay90Improve: String = "",
    val milestoneDay90Happened: String = "",
    val milestoneDay90Lessons: String = ""
)
