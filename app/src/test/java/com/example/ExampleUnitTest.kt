package com.example

import com.example.data.HabitEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testStreakCalculation() {
        // Mon..Fri all true, Sat..Sun false, priorStreak = 5
        val habit = HabitEntity(
            title = "Кодить 1 час",
            priorStreak = 5,
            mon = true, tue = true, wed = true, thu = true, fri = true, sat = false, sun = false
        )

        // On Friday (index 4), 5 days of this week + 5 prior days = 10
        assertEquals(10, habit.calculateStreak(4))

        // On Saturday (index 5) when Saturday is not done yet, streak comes from Friday = 10
        assertEquals(10, habit.calculateStreak(5))
    }

    @Test
    fun testToggledDay() {
        val habit = HabitEntity(title = "Чтение", mon = false)
        val toggled = habit.withToggledDay(0)
        assertTrue(toggled.mon)
        val toggledBack = toggled.withToggledDay(0)
        assertFalse(toggledBack.mon)
    }
}
