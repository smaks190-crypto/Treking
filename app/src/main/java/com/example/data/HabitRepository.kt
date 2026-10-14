package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class HabitRepository(private val dao: HabitDao) {
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits()

    suspend fun ensureInitialData() {
        val count = dao.countHabits()
        if (count == 0) {
            resetToDemo()
        }
    }

    suspend fun resetToDemo() {
        dao.deleteAll()
        val demoHabits = listOf(
            HabitEntity(
                id = "habit-kotlin",
                title = "Кодить 1 час (Kotlin)",
                category = "Код и IT",
                icon = "code-2",
                color = "violet",
                priorStreak = 5,
                mon = true, tue = true, wed = true, thu = true, fri = true, sat = false, sun = false
            ),
            HabitEntity(
                id = "habit-music-beat",
                title = "Написать 4 строки / бит",
                category = "Творчество",
                icon = "music-4",
                color = "cyan",
                priorStreak = 2,
                mon = true, tue = true, wed = true, thu = false, fri = true, sat = true, sun = false
            ),
            HabitEntity(
                id = "habit-reading",
                title = "Чтение 30 страниц",
                category = "Саморазвитие",
                icon = "book-open",
                color = "emerald",
                priorStreak = 4,
                mon = true, tue = true, wed = true, thu = true, fri = true, sat = true, sun = false
            ),
            HabitEntity(
                id = "habit-workout",
                title = "Тренировка / разминка",
                category = "Здоровье и спорт",
                icon = "dumbbell",
                color = "amber",
                priorStreak = 0,
                mon = true, tue = false, wed = true, thu = true, fri = false, sat = true, sun = false
            ),
            HabitEntity(
                id = "habit-nosugar",
                title = "День без сахара",
                category = "Дисциплина",
                icon = "shield-ban",
                color = "rose",
                priorStreak = 3,
                mon = true, tue = true, wed = false, thu = true, fri = true, sat = true, sun = false
            )
        )
        dao.insertHabits(demoHabits)
    }

    suspend fun toggleHabitDay(habitId: String, dayIndex: Int) {
        val habits = dao.getAllHabits().first()
        val target = habits.find { it.id == habitId } ?: return
        dao.updateHabit(target.withToggledDay(dayIndex))
    }

    suspend fun markAllDay(dayIndex: Int, completed: Boolean) {
        val habits = dao.getAllHabits().first()
        habits.forEach { habit ->
            dao.updateHabit(habit.withDayValue(dayIndex, completed))
        }
    }

    suspend fun addHabit(
        title: String,
        category: String,
        icon: String,
        color: String,
        priorStreak: Int
    ) {
        val newHabit = HabitEntity(
            title = title,
            category = category,
            icon = icon,
            color = color,
            priorStreak = priorStreak
        )
        dao.insertHabit(newHabit)
    }

    suspend fun deleteHabit(habitId: String) {
        dao.deleteHabitById(habitId)
    }
}
