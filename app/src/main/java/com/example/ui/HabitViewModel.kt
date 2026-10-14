package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitEntity
import com.example.data.HabitRepository
import com.example.data.PulseHabitDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ChartMode {
    BAR,
    WAVE
}

data class DayStat(
    val dayIndex: Int,
    val shortName: String,
    val fullName: String,
    val completedCount: Int,
    val totalCount: Int,
    val percent: Int
)

data class HabitUiState(
    val habits: List<HabitEntity> = emptyList(),
    val filteredHabits: List<HabitEntity> = emptyList(),
    val categories: List<String> = listOf("Все"),
    val selectedCategory: String = "Все",
    val activeDayIndex: Int = 4, // 0 = Mon, ..., 4 = Fri
    val realTodayIndex: Int = 4,
    val chartMode: ChartMode = ChartMode.BAR,
    val showAddDialog: Boolean = false,
    val toastMessage: String? = null,
    // 4 KPI metrics
    val todayCompletedCount: Int = 0,
    val todayTotalCount: Int = 0,
    val todayPercent: Int = 0,
    val bestStreak: Int = 0,
    val bestStreakHabitName: String = "—",
    val weeklyRatePercent: Int = 0,
    val weeklyCompletedTotal: Int = 0,
    val weeklyPossibleTotal: Int = 0,
    val perfectDaysCount: Int = 0,
    val sumStreaks: Int = 0,
    // Chart stats
    val weekStats: List<DayStat> = emptyList(),
    val bestDaySummary: String = "—",
    val averageDailyPercent: Int = 0
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HabitRepository

    private val _activeDayIndex = MutableStateFlow(calculateCurrentDayOfWeek())
    val activeDayIndex: StateFlow<Int> = _activeDayIndex.asStateFlow()

    private val _chartMode = MutableStateFlow(ChartMode.BAR)
    val chartMode: StateFlow<ChartMode> = _chartMode.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val uiState: StateFlow<HabitUiState>

    init {
        val db = PulseHabitDatabase.getDatabase(application)
        repository = HabitRepository(db.habitDao())

        viewModelScope.launch {
            repository.ensureInitialData()
        }

        val realToday = calculateCurrentDayOfWeek()

        val baseStateFlow = combine(
            repository.allHabits,
            _activeDayIndex,
            _chartMode,
            _selectedCategory
        ) { habits: List<HabitEntity>, activeDay: Int, mode: ChartMode, category: String ->
            val totalHabits = habits.size
            val categories = listOf("Все") + habits.map { it.category }.distinct()
            val filtered = if (category == "Все") habits else habits.filter { it.category == category }

            // 1. Today KPI
            val todayCompleted = habits.count { it.isCompletedOnDay(activeDay) }
            val todayPercent = if (totalHabits > 0) (todayCompleted * 100) / totalHabits else 0

            // 2. Best Streak
            var bestStreak = 0
            var bestHabitName = "Нет активных серий"
            var sumStreaks = 0
            habits.forEach { habit ->
                val streak = habit.calculateStreak(activeDay)
                sumStreaks += streak
                if (streak > bestStreak) {
                    bestStreak = streak
                    bestHabitName = habit.title
                }
            }

            // 3. Weekly rate & Perfect days
            val totalPossibleWeek = totalHabits * 7
            var completedWeek = 0
            var perfectDays = 0

            val shortDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            val fullDays = listOf(
                "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"
            )

            val weekStats = (0..6).map { dIdx ->
                val dayCompleted = habits.count { it.isCompletedOnDay(dIdx) }
                completedWeek += dayCompleted
                if (totalHabits > 0 && dayCompleted == totalHabits) {
                    perfectDays++
                }
                val pct = if (totalHabits > 0) (dayCompleted * 100) / totalHabits else 0
                DayStat(
                    dayIndex = dIdx,
                    shortName = shortDays[dIdx],
                    fullName = fullDays[dIdx],
                    completedCount = dayCompleted,
                    totalCount = totalHabits,
                    percent = pct
                )
            }

            val weekRate = if (totalPossibleWeek > 0) (completedWeek * 100) / totalPossibleWeek else 0

            // Chart summary
            var bestDayStat = weekStats.firstOrNull() ?: DayStat(0, "Пн", "Понедельник", 0, 0, 0)
            var sumPcts = 0
            weekStats.forEach {
                sumPcts += it.percent
                if (it.percent > bestDayStat.percent) {
                    bestDayStat = it
                }
            }
            val avgPercent = if (weekStats.isNotEmpty()) sumPcts / 7 else 0
            val bestSummary = if (bestDayStat.completedCount > 0) {
                "Пик продуктивности: ${bestDayStat.fullName} (${bestDayStat.percent}% — ${bestDayStat.completedCount}/$totalHabits)"
            } else {
                "Пик продуктивности: пока нет отметок"
            }

            HabitUiState(
                habits = habits,
                filteredHabits = filtered,
                categories = categories,
                selectedCategory = category,
                activeDayIndex = activeDay,
                realTodayIndex = realToday,
                chartMode = mode,
                todayCompletedCount = todayCompleted,
                todayTotalCount = totalHabits,
                todayPercent = todayPercent,
                bestStreak = bestStreak,
                bestStreakHabitName = bestHabitName,
                weeklyRatePercent = weekRate,
                weeklyCompletedTotal = completedWeek,
                weeklyPossibleTotal = totalPossibleWeek,
                perfectDaysCount = perfectDays,
                sumStreaks = sumStreaks,
                weekStats = weekStats,
                bestDaySummary = bestSummary,
                averageDailyPercent = avgPercent
            )
        }

        uiState = combine(
            baseStateFlow,
            _showAddDialog,
            _toastMessage
        ) { baseState, showDialog, toast ->
            baseState.copy(
                showAddDialog = showDialog,
                toastMessage = toast
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HabitUiState()
        )
    }

    fun setActiveDay(dayIndex: Int) {
        _activeDayIndex.value = dayIndex.coerceIn(0, 6)
    }

    fun setChartMode(mode: ChartMode) {
        _chartMode.value = mode
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setShowAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    fun toggleHabitDay(habitId: String, dayIndex: Int) {
        viewModelScope.launch {
            repository.toggleHabitDay(habitId, dayIndex)
        }
    }

    fun toggleAllForActiveDay() {
        val state = uiState.value
        val dayIndex = state.activeDayIndex
        val allCompleted = state.habits.isNotEmpty() && state.habits.all { it.isCompletedOnDay(dayIndex) }
        viewModelScope.launch {
            repository.markAllDay(dayIndex, !allCompleted)
            val dayName = when (dayIndex) {
                0 -> "понедельник"
                1 -> "вторник"
                2 -> "среду"
                3 -> "четверг"
                4 -> "пятницу"
                5 -> "субботу"
                else -> "воскресенье"
            }
            showToast(if (!allCompleted) "Все цели на $dayName выполнены!" else "Отметки на $dayName сняты")
        }
    }

    fun addHabit(title: String, category: String, icon: String, color: String, priorStreak: Int) {
        viewModelScope.launch {
            repository.addHabit(title.trim(), category, icon, color, priorStreak)
            _showAddDialog.value = false
            showToast("Привычка «${title.trim()}» добавлена!")
        }
    }

    fun deleteHabit(habitId: String, title: String) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            showToast("Привычка «$title» удалена")
        }
    }

    fun resetToDemo() {
        viewModelScope.launch {
            repository.resetToDemo()
            _activeDayIndex.value = 4 // Set focus on Friday to show rich demo streaks
            _selectedCategory.value = "Все"
            showToast("Демо-данные успешно восстановлены")
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    private fun calculateCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // Calendar.SUNDAY = 1, MONDAY = 2, ..., SATURDAY = 7
        return when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 4
        }
    }
}
