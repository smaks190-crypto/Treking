package com.example.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.R
import com.example.data.HabitEntity
import com.example.data.PulseHabitDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class HabitWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return HabitRemoteViewsFactory(applicationContext)
    }
}

class HabitRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var habits: List<HabitEntity> = emptyList()

    override fun onCreate() {
        // Initial setup
    }

    override fun onDataSetChanged() {
        try {
            val database = PulseHabitDatabase.getDatabase(context)
            habits = runBlocking {
                database.habitDao().getAllHabits().first()
            }
        } catch (e: Exception) {
            habits = emptyList()
        }
    }

    override fun onDestroy() {
        habits = emptyList()
    }

    override fun getCount(): Int = habits.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= habits.size) return null

        val habit = habits[position]
        val todayIndex = getTodayDayIndex()
        val isCompleted = habit.isCompletedOnDay(todayIndex)

        val views = RemoteViews(context.packageName, R.layout.widget_habit_item)

        // Set habit title
        views.setTextViewText(R.id.widget_item_title, habit.title)
        if (isCompleted) {
            views.setTextColor(R.id.widget_item_title, Color.parseColor("#94A3B8"))
        } else {
            views.setTextColor(R.id.widget_item_title, Color.parseColor("#F1F5F9"))
        }

        // Set category
        views.setTextViewText(R.id.widget_item_category, habit.category)

        // Calculate and set streak
        val streak = calculateHabitStreak(habit, todayIndex)
        if (streak > 0) {
            views.setViewVisibility(R.id.widget_item_streak, android.view.View.VISIBLE)
            views.setTextViewText(R.id.widget_item_streak, "🔥 $streak дн.")
        } else {
            views.setViewVisibility(R.id.widget_item_streak, android.view.View.GONE)
        }

        // Set checkbox state
        val checkboxRes = if (isCompleted) {
            R.drawable.widget_checkbox_checked
        } else {
            R.drawable.widget_checkbox_unchecked
        }
        views.setImageViewResource(R.id.widget_item_checkbox, checkboxRes)

        // Fill-in intent for toggling habit
        val toggleIntent = Intent().apply {
            putExtra(HabitWidgetProvider.EXTRA_HABIT_ID, habit.id)
            putExtra(HabitWidgetProvider.EXTRA_ACTION_TYPE, HabitWidgetProvider.ACTION_TOGGLE)
        }
        views.setOnClickFillInIntent(R.id.widget_item_checkbox, toggleIntent)

        // Fill-in intent for opening app when clicking habit text
        val openAppIntent = Intent().apply {
            putExtra(HabitWidgetProvider.EXTRA_HABIT_ID, habit.id)
            putExtra(HabitWidgetProvider.EXTRA_ACTION_TYPE, HabitWidgetProvider.ACTION_OPEN_APP)
        }
        views.setOnClickFillInIntent(R.id.widget_item_info_container, openAppIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    private fun getTodayDayIndex(): Int {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    private fun calculateHabitStreak(habit: HabitEntity, todayIndex: Int): Int {
        val week = habit.getWeekList()
        var streak = 0
        val isCompletedToday = week.getOrElse(todayIndex) { false }
        val startDay = if (isCompletedToday) todayIndex else todayIndex - 1

        var consecutive = true
        for (d in startDay downTo 0) {
            if (week.getOrElse(d) { false } && consecutive) {
                streak++
            } else {
                consecutive = false
                break
            }
        }
        if (consecutive) {
            streak += habit.priorStreak
        }
        return streak
    }
}
