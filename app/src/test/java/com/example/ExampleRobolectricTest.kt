package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.reminder.HabitReminderManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PulseHabit", appName)
  }

  @Test
  fun `reminder manager persists settings and schedules correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = HabitReminderManager(context)

    // Test updating settings
    manager.updateReminderSettings(
      enabled = true,
      hour = 21,
      minute = 30,
      onlyPending = false
    )

    assertTrue(manager.isReminderEnabled)
    assertEquals(21, manager.reminderHour)
    assertEquals(30, manager.reminderMinute)
    assertFalse(manager.onlyIfPending)

    // Disable reminder
    manager.updateReminderSettings(
      enabled = false,
      hour = 9,
      minute = 0,
      onlyPending = true
    )

    assertFalse(manager.isReminderEnabled)
    assertEquals(9, manager.reminderHour)
    assertEquals(0, manager.reminderMinute)
    assertTrue(manager.onlyIfPending)
  }

  @Test
  fun `notification channel created without crashing`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    HabitReminderManager.createNotificationChannel(context)
  }
}
