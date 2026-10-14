package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.HabitEntity
import com.example.data.PulseHabitDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_WIDGET_CLICK -> {
                val actionType = intent.getStringExtra(EXTRA_ACTION_TYPE)
                val habitId = intent.getStringExtra(EXTRA_HABIT_ID)

                if (actionType == ACTION_TOGGLE && !habitId.isNullOrEmpty()) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val database = PulseHabitDatabase.getDatabase(context)
                            val dao = database.habitDao()
                            val habits = dao.getAllHabits().first()
                            val habit = habits.find { it.id == habitId }

                            if (habit != null) {
                                val todayIndex = getTodayDayIndex()
                                val updated = habit.withToggledDay(todayIndex)
                                dao.updateHabit(updated)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            updateAllWidgets(context)
                            pendingResult.finish()
                        }
                    }
                } else if (actionType == ACTION_OPEN_APP) {
                    val appIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    context.startActivity(appIntent)
                }
            }

            ACTION_REFRESH -> {
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_CLICK = "com.example.ACTION_WIDGET_CLICK"
        const val ACTION_REFRESH = "com.example.ACTION_REFRESH_WIDGET"
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_ACTION_TYPE = "extra_action_type"
        const val ACTION_TOGGLE = "action_toggle"
        const val ACTION_OPEN_APP = "action_open_app"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_habit_layout)

            // Intent for clicking the header to open the app
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_header, openAppPendingIntent)

            // Intent for clicking the refresh button
            val refreshIntent = Intent(context, HabitWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)

            // Set up RemoteViewsService intent for ListView
            val serviceIntent = Intent(context, HabitWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widget_habit_list, serviceIntent)
            views.setEmptyView(R.id.widget_habit_list, R.id.widget_empty_view)

            // Set up pending intent template for list item clicks
            val clickIntent = Intent(context, HabitWidgetProvider::class.java).apply {
                action = ACTION_WIDGET_CLICK
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val clickPendingIntent = PendingIntent.getBroadcast(
                context,
                100 + appWidgetId,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_habit_list, clickPendingIntent)

            // Calculate and set status summary text asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = PulseHabitDatabase.getDatabase(context)
                    val habits = database.habitDao().getAllHabits().first()
                    val todayIndex = getTodayDayIndex()
                    val total = habits.size
                    val completed = habits.count { it.isCompletedOnDay(todayIndex) }
                    val percent = if (total > 0) (completed * 100) / total else 0
                    val dayName = getDayName(todayIndex)

                    val statusText = if (total > 0) {
                        "$dayName: $completed/$total выполнено ($percent%)"
                    } else {
                        "$dayName: нет привычек"
                    }

                    views.setTextViewText(R.id.widget_status_text, statusText)
                    appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_habit_list)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, HabitWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (appWidgetId in appWidgetIds) {
                updateWidget(context, appWidgetManager, appWidgetId)
            }
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_habit_list)
        }

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

        private fun getDayName(dayIndex: Int): String {
            return when (dayIndex) {
                0 -> "Понедельник"
                1 -> "Вторник"
                2 -> "Среда"
                3 -> "Четверг"
                4 -> "Пятница"
                5 -> "Суббота"
                6 -> "Воскресенье"
                else -> "Сегодня"
            }
        }
    }
}
