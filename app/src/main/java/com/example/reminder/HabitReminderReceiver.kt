package com.example.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.PulseHabitDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderManager = HabitReminderManager(context)

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Device rebooted or app updated: restore scheduled reminder if enabled
                if (reminderManager.isReminderEnabled) {
                    reminderManager.scheduleDailyAlarm()
                }
            }

            ACTION_HABIT_REMINDER -> {
                if (!reminderManager.isReminderEnabled) return

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val database = PulseHabitDatabase.getDatabase(context)
                        val habits = database.habitDao().getAllHabits().first()

                        val todayIndex = getTodayDayIndex()
                        val pendingHabits = habits.filter { !it.isCompletedOnDay(todayIndex) }

                        // Check if we should only notify if there are pending habits
                        if (reminderManager.onlyIfPending && pendingHabits.isEmpty()) {
                            // All habits are completed, skip notification!
                            return@launch
                        }

                        // Build and display notification
                        HabitReminderManager.createNotificationChannel(context)

                        val activityIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        val contentPendingIntent = PendingIntent.getActivity(
                            context,
                            0,
                            activityIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        val title = if (pendingHabits.isNotEmpty()) {
                            "PulseHabit • Осталось ${pendingHabits.size} привычек! 🔥"
                        } else {
                            "PulseHabit • Отличная работа! 🎉"
                        }

                        val contentText = if (pendingHabits.isNotEmpty()) {
                            "Не забудьте закрыть цели на сегодня: ${pendingHabits.take(2).joinToString(", ") { it.title }}"
                        } else {
                            "Все привычки на сегодня выполнены! Продолжайте в том же духе."
                        }

                        val bigText = buildString {
                            if (pendingHabits.isNotEmpty()) {
                                append("Остались невыполненными следующие привычки:\n")
                                pendingHabits.forEach { habit ->
                                    append("• ${habit.title} (${habit.category})\n")
                                }
                                append("\nОткройте PulseHabit, чтобы отметить и сохранить серию!")
                            } else {
                                append("Все цели на сегодня достигнуты. Ваш прогресс сохранен!")
                            }
                        }

                        val notification = NotificationCompat.Builder(context, HabitReminderManager.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setContentTitle(title)
                            .setContentText(contentText)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setContentIntent(contentPendingIntent)
                            .build()

                        NotificationManagerCompat.from(context)
                            .notify(HabitReminderManager.NOTIFICATION_ID, notification)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        // Always reschedule for the next day
                        reminderManager.scheduleDailyAlarm()
                        pendingResult.finish()
                    }
                }
            }
        }
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

    companion object {
        const val ACTION_HABIT_REMINDER = "com.example.ACTION_HABIT_REMINDER"
    }
}
