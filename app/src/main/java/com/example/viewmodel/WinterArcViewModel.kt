package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ArcStrategyEntity
import com.example.data.local.DailyReflectionEntity
import com.example.data.local.TaskEntity
import com.example.data.local.TimeSlot
import com.example.data.local.WeeklyReviewEntity
import com.example.data.repository.WinterArcRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek

enum class ArcTab(val label: String) {
    TODAY("TODAY"),
    REVIEW("REVIEW"),
    ARC("ARC"),
    BACKUP("BACKUP")
}

data class TodayUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val dateKey: String = LocalDate.now().toString(),
    val displayDateString: String = "",
    val tasks: List<TaskEntity> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val progress: Float = 0f,
    val top5Map: Map<Int, TaskEntity> = emptyMap(),
    val availableTasksForTop5: List<TaskEntity> = emptyList(),
    val morningTasks: List<TaskEntity> = emptyList(),
    val afternoonTasks: List<TaskEntity> = emptyList(),
    val eveningTasks: List<TaskEntity> = emptyList(),
    val nightTasks: List<TaskEntity> = emptyList()
)

data class WeeklyStats(
    val weekKey: String = "",
    val weekRangeDisplay: String = "",
    val completedTasks: Int = 0,
    val totalTasks: Int = 0
)

class WinterArcViewModel(
    private val repository: WinterArcRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

    private val _currentTab = MutableStateFlow(ArcTab.TODAY)
    val currentTab: StateFlow<ArcTab> = _currentTab.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _backupJson = MutableStateFlow("")
    val backupJson: StateFlow<String> = _backupJson.asStateFlow()

    private val _backupStatusMessage = MutableStateFlow<String?>(null)
    val backupStatusMessage: StateFlow<String?> = _backupStatusMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayUiState: StateFlow<TodayUiState> = _selectedDate
        .flatMapLatest { date ->
            val dateKey = date.toString() // "YYYY-MM-DD"
            val displayDate = if (date == LocalDate.now()) {
                "Today · ${date.format(dateFormatter)}"
            } else {
                date.format(dateFormatter)
            }

            repository.getTasksForDate(dateKey).combine(repository.getAllTasks()) { dateTasks, _ ->
                val taskComparator = Comparator<TaskEntity> { a, b ->
                    val aHasTime = a.scheduledTime.isNotBlank()
                    val bHasTime = b.scheduledTime.isNotBlank()
                    when {
                        aHasTime && bHasTime -> a.scheduledTime.compareTo(b.scheduledTime)
                        aHasTime && !bHasTime -> -1
                        !aHasTime && bHasTime -> 1
                        else -> a.createdAt.compareTo(b.createdAt)
                    }
                }

                val total = dateTasks.size
                val completed = dateTasks.count { it.isCompleted }
                val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f

                val top5Map = mutableMapOf<Int, TaskEntity>()
                val top5Candidates = dateTasks.filter { it.isTop5 }
                top5Candidates.forEach { t ->
                    if (t.top5Index in 0..4) {
                        top5Map[t.top5Index] = t
                    }
                }
                // Fill any unindexed top 5 into next empty slot 0..4
                top5Candidates.filter { it.top5Index !in 0..4 }.forEach { t ->
                    val nextSlot = (0..4).firstOrNull { it !in top5Map }
                    if (nextSlot != null) {
                        top5Map[nextSlot] = t
                    }
                }

                val availableForTop5 = dateTasks.filter { !it.isTop5 }.sortedWith(taskComparator)

                TodayUiState(
                    selectedDate = date,
                    dateKey = dateKey,
                    displayDateString = displayDate,
                    tasks = dateTasks.sortedWith(taskComparator),
                    completedCount = completed,
                    totalCount = total,
                    progress = progress,
                    top5Map = top5Map,
                    availableTasksForTop5 = availableForTop5,
                    morningTasks = dateTasks.filter { it.slot.equals(TimeSlot.MORNING.name, ignoreCase = true) }.sortedWith(taskComparator),
                    afternoonTasks = dateTasks.filter { it.slot.equals(TimeSlot.AFTERNOON.name, ignoreCase = true) }.sortedWith(taskComparator),
                    eveningTasks = dateTasks.filter { it.slot.equals(TimeSlot.EVENING.name, ignoreCase = true) }.sortedWith(taskComparator),
                    nightTasks = dateTasks.filter { it.slot.equals(TimeSlot.NIGHT.name, ignoreCase = true) }.sortedWith(taskComparator)
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TodayUiState()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyReflection: StateFlow<DailyReflectionEntity> = _selectedDate
        .flatMapLatest { date ->
            val dateKey = date.toString()
            repository.getReflection(dateKey)
        }
        .combine(_selectedDate) { reflection, date ->
            reflection ?: DailyReflectionEntity(dateKey = date.toString())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailyReflectionEntity(dateKey = LocalDate.now().toString())
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklyReview: StateFlow<WeeklyReviewEntity> = _selectedDate
        .flatMapLatest { date ->
            val weekKey = getWeekKey(date)
            repository.getWeeklyReview(weekKey)
        }
        .combine(_selectedDate) { review, date ->
            review ?: WeeklyReviewEntity(weekKey = getWeekKey(date))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            WeeklyReviewEntity(weekKey = getWeekKey(LocalDate.now()))
        )

    val weeklyStats: StateFlow<WeeklyStats> = _selectedDate
        .combine(repository.getAllTasks()) { date, allTasks ->
            val weekKey = getWeekKey(date)
            val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            val weekTasks = allTasks.filter { task ->
                try {
                    val taskDate = LocalDate.parse(task.dateKey)
                    !taskDate.isBefore(monday) && !taskDate.isAfter(sunday)
                } catch (e: Exception) {
                    false
                }
            }

            val weekRange = "${monday.format(DateTimeFormatter.ofPattern("MMM d"))} – ${sunday.format(DateTimeFormatter.ofPattern("MMM d"))}"
            WeeklyStats(
                weekKey = weekKey,
                weekRangeDisplay = "Week ${date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)} · $weekRange",
                completedTasks = weekTasks.count { it.isCompleted },
                totalTasks = weekTasks.size
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            WeeklyStats()
        )

    val currentStreak: StateFlow<Int> = repository.getAllTasks()
        .combine(_selectedDate) { allTasks, _ ->
            val completedDates = allTasks
                .filter { it.isCompleted }
                .mapNotNull {
                    try { LocalDate.parse(it.dateKey) } catch (e: Exception) { null }
                }
                .toSet()

            val today = LocalDate.now()
            var streak = 0
            var checkDate = today

            // If today has completed tasks, count today and previous consecutive days
            // If today doesn't have completed tasks yet, check if streak from yesterday is still active
            if (!completedDates.contains(today)) {
                checkDate = today.minusDays(1)
            }

            while (completedDates.contains(checkDate)) {
                streak++
                checkDate = checkDate.minusDays(1)
            }
            streak
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val arcStrategy: StateFlow<ArcStrategyEntity> = repository.getArcStrategy()
        .combine(MutableStateFlow(Unit)) { strategy, _ ->
            strategy ?: ArcStrategyEntity(
                id = 1,
                startDate = LocalDate.now().toString(),
                endDate = LocalDate.now().plusDays(90).toString(),
                mainGoal = ""
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ArcStrategyEntity(
                id = 1,
                startDate = LocalDate.now().toString(),
                endDate = LocalDate.now().plusDays(90).toString(),
                mainGoal = ""
            )
        )

    init {
        refreshBackupJson()
    }

    fun selectTab(tab: ArcTab) {
        _currentTab.value = tab
        if (tab == ArcTab.BACKUP) {
            refreshBackupJson()
        }
    }

    fun navigateDate(deltaDays: Long) {
        _selectedDate.value = _selectedDate.value.plusDays(deltaDays)
    }

    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun addTask(
        title: String,
        slot: TimeSlot,
        isTop5: Boolean,
        top5Index: Int = -1,
        scheduledTime: String = ""
    ) {
        viewModelScope.launch {
            val dateKey = _selectedDate.value.toString()
            val newTask = TaskEntity(
                dateKey = dateKey,
                title = title.trim(),
                slot = slot.name,
                isCompleted = false,
                isTop5 = isTop5,
                top5Index = if (isTop5) top5Index else -1,
                scheduledTime = scheduledTime.trim()
            )
            repository.insertTask(newTask)
            refreshBackupJson()
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
            refreshBackupJson()
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            refreshBackupJson()
        }
    }

    fun toggleTop5(task: TaskEntity) {
        viewModelScope.launch {
            val newIsTop5 = !task.isTop5
            val newIndex = if (newIsTop5) {
                // Find first free slot
                val currentTop5Map = todayUiState.value.top5Map
                (0..4).firstOrNull { it !in currentTop5Map } ?: 0
            } else {
                -1
            }
            repository.updateTask(task.copy(isTop5 = newIsTop5, top5Index = newIndex))
            refreshBackupJson()
        }
    }

    fun assignExistingToTop5(task: TaskEntity, slotIndex: Int) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isTop5 = true, top5Index = slotIndex))
            refreshBackupJson()
        }
    }

    fun removeFromTop5(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isTop5 = false, top5Index = -1))
            refreshBackupJson()
        }
    }

    fun updateReflection(
        wentWell: String? = null,
        distracted: String? = null,
        improveTomorrow: String? = null,
        proudOf: String? = null,
        mood: String? = null
    ) {
        viewModelScope.launch {
            val current = dailyReflection.value
            val updated = current.copy(
                wentWell = wentWell ?: current.wentWell,
                distracted = distracted ?: current.distracted,
                improveTomorrow = improveTomorrow ?: current.improveTomorrow,
                proudOf = proudOf ?: current.proudOf,
                mood = mood ?: current.mood
            )
            repository.saveReflection(updated)
            refreshBackupJson()
        }
    }

    fun resetDailyMood() {
        updateReflection(mood = "")
    }

    fun updateWeeklyReview(
        biggestWin: String? = null,
        biggestProblem: String? = null,
        whatWorked: String? = null,
        whatToChange: String? = null,
        nextWeekTop5: String? = null,
        learnedAboutSelf: String? = null
    ) {
        viewModelScope.launch {
            val current = weeklyReview.value
            val updated = current.copy(
                biggestWin = biggestWin ?: current.biggestWin,
                biggestProblem = biggestProblem ?: current.biggestProblem,
                whatWorked = whatWorked ?: current.whatWorked,
                whatToChange = whatToChange ?: current.whatToChange,
                nextWeekTop5 = nextWeekTop5 ?: current.nextWeekTop5,
                learnedAboutSelf = learnedAboutSelf ?: current.learnedAboutSelf
            )
            repository.saveWeeklyReview(updated)
            refreshBackupJson()
        }
    }

    fun updateArcStrategy(
        startDate: String? = null,
        endDate: String? = null,
        mainGoal: String? = null,
        focusTagsCsv: String? = null,
        day1Improve: String? = null,
        day1Happened: String? = null,
        day1Lessons: String? = null,
        day30Improve: String? = null,
        day30Happened: String? = null,
        day30Lessons: String? = null,
        day60Improve: String? = null,
        day60Happened: String? = null,
        day60Lessons: String? = null,
        day90Improve: String? = null,
        day90Happened: String? = null,
        day90Lessons: String? = null
    ) {
        viewModelScope.launch {
            val current = arcStrategy.value
            val updated = current.copy(
                startDate = startDate ?: current.startDate,
                endDate = endDate ?: current.endDate,
                mainGoal = mainGoal ?: current.mainGoal,
                focusTagsCsv = focusTagsCsv ?: current.focusTagsCsv,
                milestoneDay1Improve = day1Improve ?: current.milestoneDay1Improve,
                milestoneDay1Happened = day1Happened ?: current.milestoneDay1Happened,
                milestoneDay1Lessons = day1Lessons ?: current.milestoneDay1Lessons,
                milestoneDay30Improve = day30Improve ?: current.milestoneDay30Improve,
                milestoneDay30Happened = day30Happened ?: current.milestoneDay30Happened,
                milestoneDay30Lessons = day30Lessons ?: current.milestoneDay30Lessons,
                milestoneDay60Improve = day60Improve ?: current.milestoneDay60Improve,
                milestoneDay60Happened = day60Happened ?: current.milestoneDay60Happened,
                milestoneDay60Lessons = day60Lessons ?: current.milestoneDay60Lessons,
                milestoneDay90Improve = day90Improve ?: current.milestoneDay90Improve,
                milestoneDay90Happened = day90Happened ?: current.milestoneDay90Happened,
                milestoneDay90Lessons = day90Lessons ?: current.milestoneDay90Lessons
            )
            repository.saveArcStrategy(updated)
            refreshBackupJson()
        }
    }

    fun refreshBackupJson() {
        viewModelScope.launch {
            _backupJson.value = repository.exportJsonState()
        }
    }

    fun restoreBackup(jsonString: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.restoreFromJson(jsonString)
            if (result.isSuccess) {
                refreshBackupJson()
                _backupStatusMessage.value = "Data successfully restored!"
                onResult(true, "Data successfully restored!")
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Invalid JSON format"
                _backupStatusMessage.value = "Failed to restore: $errorMsg"
                onResult(false, errorMsg)
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            refreshBackupJson()
            _backupStatusMessage.value = "All data erased."
        }
    }

    private fun getWeekKey(date: LocalDate): String {
        val weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val year = date.get(IsoFields.WEEK_BASED_YEAR)
        return String.format("%04d-W%02d", year, weekNumber)
    }

    class Factory(private val repository: WinterArcRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WinterArcViewModel(repository) as T
        }
    }
}
