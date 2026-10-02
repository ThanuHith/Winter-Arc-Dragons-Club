package com.example.data.repository

import com.example.data.local.ArcStrategyEntity
import com.example.data.local.DailyReflectionEntity
import com.example.data.local.TaskEntity
import com.example.data.local.WeeklyReviewEntity
import com.example.data.local.WinterArcDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class WinterArcRepository(private val dao: WinterArcDao) {

    fun getTasksForDate(dateKey: String): Flow<List<TaskEntity>> = dao.getTasksForDate(dateKey)

    fun getAllTasks(): Flow<List<TaskEntity>> = dao.getAllTasks()

    suspend fun getTaskById(id: Long): TaskEntity? = withContext(Dispatchers.IO) {
        dao.getTaskById(id)
    }

    suspend fun getPendingReminderTasks(currentTimeMillis: Long): List<TaskEntity> = withContext(Dispatchers.IO) {
        dao.getPendingReminderTasks(currentTimeMillis)
    }

    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        dao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        dao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        dao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteTaskById(id)
    }

    fun getReflection(dateKey: String): Flow<DailyReflectionEntity?> = dao.getReflection(dateKey)

    suspend fun saveReflection(reflection: DailyReflectionEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateReflection(reflection)
    }

    fun getWeeklyReview(weekKey: String): Flow<WeeklyReviewEntity?> = dao.getWeeklyReview(weekKey)

    suspend fun saveWeeklyReview(review: WeeklyReviewEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateWeeklyReview(review)
    }

    fun getArcStrategy(): Flow<ArcStrategyEntity?> = dao.getArcStrategy()

    suspend fun saveArcStrategy(strategy: ArcStrategyEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateArcStrategy(strategy)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        dao.clearAllTasks()
        dao.clearAllReflections()
        dao.clearAllWeeklyReviews()
        dao.clearArcStrategy()
    }

    suspend fun exportJsonState(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "Dragons Winter Arc")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val tasksArray = JSONArray()
        dao.getAllTasksDirect().forEach { task ->
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("dateKey", task.dateKey)
            obj.put("title", task.title)
            obj.put("slot", task.slot)
            obj.put("isCompleted", task.isCompleted)
            obj.put("isTop5", task.isTop5)
            obj.put("top5Index", task.top5Index)
            obj.put("scheduledTime", task.scheduledTime)
            if (task.scheduledTimeMillis != null) {
                obj.put("scheduledTimeMillis", task.scheduledTimeMillis)
            }
            obj.put("createdAt", task.createdAt)
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        val reflectionsArray = JSONArray()
        dao.getAllReflectionsDirect().forEach { ref ->
            val obj = JSONObject()
            obj.put("dateKey", ref.dateKey)
            obj.put("wentWell", ref.wentWell)
            obj.put("distracted", ref.distracted)
            obj.put("improveTomorrow", ref.improveTomorrow)
            obj.put("proudOf", ref.proudOf)
            obj.put("mood", ref.mood)
            reflectionsArray.put(obj)
        }
        root.put("dailyReflections", reflectionsArray)

        val reviewsArray = JSONArray()
        dao.getAllWeeklyReviewsDirect().forEach { rev ->
            val obj = JSONObject()
            obj.put("weekKey", rev.weekKey)
            obj.put("biggestWin", rev.biggestWin)
            obj.put("biggestProblem", rev.biggestProblem)
            obj.put("whatWorked", rev.whatWorked)
            obj.put("whatToChange", rev.whatToChange)
            obj.put("nextWeekTop5", rev.nextWeekTop5)
            obj.put("learnedAboutSelf", rev.learnedAboutSelf)
            reviewsArray.put(obj)
        }
        root.put("weeklyReviews", reviewsArray)

        val arcStrategy = dao.getArcStrategyDirect()
        if (arcStrategy != null) {
            val arcObj = JSONObject()
            arcObj.put("startDate", arcStrategy.startDate)
            arcObj.put("endDate", arcStrategy.endDate)
            arcObj.put("mainGoal", arcStrategy.mainGoal)
            arcObj.put("focusTagsCsv", arcStrategy.focusTagsCsv)
            arcObj.put("milestoneDay1Improve", arcStrategy.milestoneDay1Improve)
            arcObj.put("milestoneDay1Happened", arcStrategy.milestoneDay1Happened)
            arcObj.put("milestoneDay1Lessons", arcStrategy.milestoneDay1Lessons)
            arcObj.put("milestoneDay30Improve", arcStrategy.milestoneDay30Improve)
            arcObj.put("milestoneDay30Happened", arcStrategy.milestoneDay30Happened)
            arcObj.put("milestoneDay30Lessons", arcStrategy.milestoneDay30Lessons)
            arcObj.put("milestoneDay60Improve", arcStrategy.milestoneDay60Improve)
            arcObj.put("milestoneDay60Happened", arcStrategy.milestoneDay60Happened)
            arcObj.put("milestoneDay60Lessons", arcStrategy.milestoneDay60Lessons)
            arcObj.put("milestoneDay90Improve", arcStrategy.milestoneDay90Improve)
            arcObj.put("milestoneDay90Happened", arcStrategy.milestoneDay90Happened)
            arcObj.put("milestoneDay90Lessons", arcStrategy.milestoneDay90Lessons)
            root.put("arcStrategy", arcObj)
        } else {
            root.put("arcStrategy", JSONObject.NULL)
        }

        root.toString(2)
    }

    suspend fun restoreFromJson(jsonString: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)

            val taskList = mutableListOf<TaskEntity>()
            if (root.has("tasks")) {
                val tasksArray = root.getJSONArray("tasks")
                for (i in 0 until tasksArray.length()) {
                    val obj = tasksArray.getJSONObject(i)
                    taskList.add(
                        TaskEntity(
                            id = if (obj.has("id")) obj.getLong("id") else 0L,
                            dateKey = obj.optString("dateKey", ""),
                            title = obj.optString("title", ""),
                            slot = obj.optString("slot", "MORNING"),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            isTop5 = obj.optBoolean("isTop5", false),
                            top5Index = obj.optInt("top5Index", -1),
                            scheduledTime = obj.optString("scheduledTime", ""),
                            scheduledTimeMillis = if (obj.has("scheduledTimeMillis") && !obj.isNull("scheduledTimeMillis")) obj.optLong("scheduledTimeMillis") else null,
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val reflectionList = mutableListOf<DailyReflectionEntity>()
            if (root.has("dailyReflections")) {
                val refArray = root.getJSONArray("dailyReflections")
                for (i in 0 until refArray.length()) {
                    val obj = refArray.getJSONObject(i)
                    reflectionList.add(
                        DailyReflectionEntity(
                            dateKey = obj.optString("dateKey", ""),
                            wentWell = obj.optString("wentWell", ""),
                            distracted = obj.optString("distracted", ""),
                            improveTomorrow = obj.optString("improveTomorrow", ""),
                            proudOf = obj.optString("proudOf", ""),
                            mood = obj.optString("mood", "")
                        )
                    )
                }
            }

            val reviewList = mutableListOf<WeeklyReviewEntity>()
            if (root.has("weeklyReviews")) {
                val revArray = root.getJSONArray("weeklyReviews")
                for (i in 0 until revArray.length()) {
                    val obj = revArray.getJSONObject(i)
                    reviewList.add(
                        WeeklyReviewEntity(
                            weekKey = obj.optString("weekKey", ""),
                            biggestWin = obj.optString("biggestWin", ""),
                            biggestProblem = obj.optString("biggestProblem", ""),
                            whatWorked = obj.optString("whatWorked", ""),
                            whatToChange = obj.optString("whatToChange", ""),
                            nextWeekTop5 = obj.optString("nextWeekTop5", ""),
                            learnedAboutSelf = obj.optString("learnedAboutSelf", "")
                        )
                    )
                }
            }

            var arcStrategy: ArcStrategyEntity? = null
            if (root.has("arcStrategy") && !root.isNull("arcStrategy")) {
                val obj = root.getJSONObject("arcStrategy")
                arcStrategy = ArcStrategyEntity(
                    id = 1,
                    startDate = obj.optString("startDate", ""),
                    endDate = obj.optString("endDate", ""),
                    mainGoal = obj.optString("mainGoal", ""),
                    focusTagsCsv = obj.optString("focusTagsCsv", ""),
                    milestoneDay1Improve = obj.optString("milestoneDay1Improve", ""),
                    milestoneDay1Happened = obj.optString("milestoneDay1Happened", ""),
                    milestoneDay1Lessons = obj.optString("milestoneDay1Lessons", ""),
                    milestoneDay30Improve = obj.optString("milestoneDay30Improve", ""),
                    milestoneDay30Happened = obj.optString("milestoneDay30Happened", ""),
                    milestoneDay30Lessons = obj.optString("milestoneDay30Lessons", ""),
                    milestoneDay60Improve = obj.optString("milestoneDay60Improve", ""),
                    milestoneDay60Happened = obj.optString("milestoneDay60Happened", ""),
                    milestoneDay60Lessons = obj.optString("milestoneDay60Lessons", ""),
                    milestoneDay90Improve = obj.optString("milestoneDay90Improve", ""),
                    milestoneDay90Happened = obj.optString("milestoneDay90Happened", ""),
                    milestoneDay90Lessons = obj.optString("milestoneDay90Lessons", "")
                )
            }

            // Apply updates
            dao.clearAllTasks()
            dao.clearAllReflections()
            dao.clearAllWeeklyReviews()
            dao.clearArcStrategy()

            if (taskList.isNotEmpty()) dao.insertAllTasks(taskList)
            if (reflectionList.isNotEmpty()) dao.insertAllReflections(reflectionList)
            if (reviewList.isNotEmpty()) dao.insertAllWeeklyReviews(reviewList)
            if (arcStrategy != null) dao.insertOrUpdateArcStrategy(arcStrategy)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
