package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WinterArcDao {

    // --- Tasks ---
    @Query("SELECT * FROM tasks WHERE dateKey = :dateKey ORDER BY createdAt ASC")
    fun getTasksForDate(dateKey: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY createdAt ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY createdAt ASC")
    suspend fun getAllTasksDirect(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

    // --- Daily Reflections ---
    @Query("SELECT * FROM daily_reflections WHERE dateKey = :dateKey LIMIT 1")
    fun getReflection(dateKey: String): Flow<DailyReflectionEntity?>

    @Query("SELECT * FROM daily_reflections WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getReflectionDirect(dateKey: String): DailyReflectionEntity?

    @Query("SELECT * FROM daily_reflections")
    suspend fun getAllReflectionsDirect(): List<DailyReflectionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReflection(reflection: DailyReflectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReflections(reflections: List<DailyReflectionEntity>)

    @Query("DELETE FROM daily_reflections")
    suspend fun clearAllReflections()

    // --- Weekly Reviews ---
    @Query("SELECT * FROM weekly_reviews WHERE weekKey = :weekKey LIMIT 1")
    fun getWeeklyReview(weekKey: String): Flow<WeeklyReviewEntity?>

    @Query("SELECT * FROM weekly_reviews WHERE weekKey = :weekKey LIMIT 1")
    suspend fun getWeeklyReviewDirect(weekKey: String): WeeklyReviewEntity?

    @Query("SELECT * FROM weekly_reviews")
    suspend fun getAllWeeklyReviewsDirect(): List<WeeklyReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWeeklyReview(review: WeeklyReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWeeklyReviews(reviews: List<WeeklyReviewEntity>)

    @Query("DELETE FROM weekly_reviews")
    suspend fun clearAllWeeklyReviews()

    // --- Arc Strategy ---
    @Query("SELECT * FROM arc_strategy WHERE id = 1 LIMIT 1")
    fun getArcStrategy(): Flow<ArcStrategyEntity?>

    @Query("SELECT * FROM arc_strategy WHERE id = 1 LIMIT 1")
    suspend fun getArcStrategyDirect(): ArcStrategyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateArcStrategy(strategy: ArcStrategyEntity)

    @Query("DELETE FROM arc_strategy")
    suspend fun clearArcStrategy()
}
