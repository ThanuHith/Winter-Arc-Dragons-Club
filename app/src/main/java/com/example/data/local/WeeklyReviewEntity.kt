package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_reviews")
data class WeeklyReviewEntity(
    @PrimaryKey
    val weekKey: String, // e.g. "2026-W40"
    val biggestWin: String = "",
    val biggestProblem: String = "",
    val whatWorked: String = "",
    val whatToChange: String = "",
    val nextWeekTop5: String = "",
    val learnedAboutSelf: String = ""
)
