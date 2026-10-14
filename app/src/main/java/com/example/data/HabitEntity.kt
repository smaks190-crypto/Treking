package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String = "Код и IT",
    val icon: String = "code-2",
    val color: String = "violet",
    val priorStreak: Int = 0,
    val mon: Boolean = false,
    val tue: Boolean = false,
    val wed: Boolean = false,
    val thu: Boolean = false,
    val fri: Boolean = false,
    val sat: Boolean = false,
    val sun: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getWeekList(): List<Boolean> = listOf(mon, tue, wed, thu, fri, sat, sun)

    fun withToggledDay(dayIndex: Int): HabitEntity {
        return when (dayIndex) {
            0 -> copy(mon = !mon)
            1 -> copy(tue = !tue)
            2 -> copy(wed = !wed)
            3 -> copy(thu = !thu)
            4 -> copy(fri = !fri)
            5 -> copy(sat = !sat)
            6 -> copy(sun = !sun)
            else -> this
        }
    }

    fun withDayValue(dayIndex: Int, value: Boolean): HabitEntity {
        return when (dayIndex) {
            0 -> copy(mon = value)
            1 -> copy(tue = value)
            2 -> copy(wed = value)
            3 -> copy(thu = value)
            4 -> copy(fri = value)
            5 -> copy(sat = value)
            6 -> copy(sun = value)
            else -> this
        }
    }

    fun isCompletedOnDay(dayIndex: Int): Boolean {
        return when (dayIndex) {
            0 -> mon
            1 -> tue
            2 -> wed
            3 -> thu
            4 -> fri
            5 -> sat
            6 -> sun
            else -> false
        }
    }

    fun calculateStreak(upToDayIndex: Int): Int {
        val week = getWeekList()
        var startIdx = upToDayIndex.coerceIn(0, 6)
        if (!week[startIdx]) {
            startIdx -= 1
        }
        if (startIdx < 0) {
            return priorStreak
        }

        var streakCount = 0
        var reachedMonday = false
        for (d in startIdx downTo 0) {
            if (week[d]) {
                streakCount++
                if (d == 0) reachedMonday = true
            } else {
                break
            }
        }

        if (reachedMonday) {
            streakCount += priorStreak
        }
        return streakCount
    }
}
