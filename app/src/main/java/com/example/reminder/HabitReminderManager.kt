package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

class HabitReminderManager(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var reminderHour: Int
        get() = prefs.getInt(KEY_HOUR, 20) // Default 20:00 (8 PM)
        set(value) = prefs.edit().putInt(KEY_HOUR, value).apply()

    var reminderMinute: Int
        get() = prefs.getInt(KEY_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_MINUTE, value).apply()

    var onlyIfPending: Boolean
        get() = prefs.getBoolean(KEY_ONLY_PENDING, true)
        set(value) = prefs.edit().putBoolean(KEY_ONLY_PENDING, value).apply()

    fun updateReminderSettings(enabled: Boolean, hour: Int, minute: Int, onlyPending: Boolean) {
        isReminderEnabled = enabled
        reminderHour = hour
        reminderMinute = minute
        onlyIfPending = onlyPending

        if (enabled) {
            scheduleDailyAlarm()
        } else {
            cancelDailyAlarm()
        }
    }

    fun scheduleDailyAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            action = HabitReminderReceiver.ACTION_HABIT_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminderHour)
            set(Calendar.MINUTE, reminderMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for exact alarm permission constraints on Android 12+
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelDailyAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            action = HabitReminderReceiver.ACTION_HABIT_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun sendTestNotification() {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedTime = String.format("%02d:%02d", reminderHour, reminderMinute)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle("PulseHabit • Тестовое напоминание 🔔")
            .setContentText("Напоминания активны на $formattedTime. Ваши привычки под контролем!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Ежедневные уведомления настроены на $formattedTime.\nPulseHabit вовремя напомнит о незавершенных привычках, чтобы ваш стрик не прерывался!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(TEST_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val PREFS_NAME = "habit_reminder_prefs"
        const val KEY_ENABLED = "key_reminder_enabled"
        const val KEY_HOUR = "key_reminder_hour"
        const val KEY_MINUTE = "key_reminder_minute"
        const val KEY_ONLY_PENDING = "key_only_pending"

        const val CHANNEL_ID = "habit_reminders_channel"
        const val REMINDER_REQUEST_CODE = 2001
        const val NOTIFICATION_ID = 3001
        const val TEST_NOTIFICATION_ID = 3002

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_channel_name)
                val descriptionText = context.getString(R.string.notification_channel_desc)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableVibration(true)
                }
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
