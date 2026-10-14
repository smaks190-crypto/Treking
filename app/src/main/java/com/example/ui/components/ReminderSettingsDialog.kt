package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.reminder.HabitReminderManager
import com.example.ui.theme.BgCard
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderSettingsDialog(
    reminderManager: HabitReminderManager,
    onDismiss: () -> Unit,
    onSaveSuccess: (String) -> Unit
) {
    val context = LocalContext.current

    var isEnabled by remember { mutableStateOf(reminderManager.isReminderEnabled) }
    var selectedHour by remember { mutableIntStateOf(reminderManager.reminderHour) }
    var selectedMinute by remember { mutableIntStateOf(reminderManager.reminderMinute) }
    var onlyIfPending by remember { mutableStateOf(reminderManager.onlyIfPending) }

    // Android 13+ Notification Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isEnabled = true
            Toast.makeText(context, "Разрешение на уведомления получено", Toast.LENGTH_SHORT).show()
        } else {
            isEnabled = false
            Toast.makeText(
                context,
                "Для напоминаний необходимо разрешение на отправку уведомлений",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val requestNotificationPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                isEnabled = true
            }
        } else {
            isEnabled = true
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF131D31),
                            Color(0xFF0B0F19)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonViolet.copy(alpha = 0.5f),
                            BorderDark
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonViolet.copy(alpha = 0.15f))
                                .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Напоминания",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Сохраняйте серию привычек",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Toggle Reminders Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF162238))
                        .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ежедневное напоминание",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isEnabled) "Активно каждый день" else "Напоминания отключены",
                            color = if (isEnabled) NeonCyan else TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                requestNotificationPermission()
                            } else {
                                isEnabled = false
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonViolet,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.testTag("switch_reminder_enable")
                    )
                }

                if (isEnabled) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ВРЕМЯ НАПОМИНАНИЯ",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Quick Chips
                    val presets = listOf(
                        Triple(9, 0, "09:00 Утро"),
                        Triple(14, 0, "14:00 День"),
                        Triple(20, 0, "20:00 Вечер"),
                        Triple(22, 0, "22:00 Сон")
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { (h, m, label) ->
                            val isSelected = selectedHour == h && selectedMinute == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) NeonViolet.copy(alpha = 0.25f)
                                        else Color(0xFF1E293B)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonViolet else BorderDark,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedHour = h
                                        selectedMinute = m
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) NeonCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom Time Pickers (Hour and Minute adjusters)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour Selector
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    selectedHour = if (selectedHour > 0) selectedHour - 1 else 23
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Минус час", tint = TextMuted)
                            }

                            Text(
                                text = String.format("%02d", selectedHour),
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = {
                                    selectedHour = if (selectedHour < 23) selectedHour + 1 else 0
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Плюс час", tint = TextMuted)
                            }
                        }

                        Text(
                            text = ":",
                            color = NeonViolet,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Minute Selector
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    selectedMinute = if (selectedMinute >= 5) selectedMinute - 5 else 55
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Минус 5 мин", tint = TextMuted)
                            }

                            Text(
                                text = String.format("%02d", selectedMinute),
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = {
                                    selectedMinute = if (selectedMinute <= 50) selectedMinute + 5 else 0
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Плюс 5 мин", tint = TextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Condition: only if pending
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onlyIfPending = !onlyIfPending }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = onlyIfPending,
                            onCheckedChange = { onlyIfPending = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonViolet,
                                uncheckedColor = TextMuted,
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "Напоминать только о незавершенных привычках",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Test Notification Button
                    OutlinedButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    return@OutlinedButton
                                }
                            }
                            reminderManager.reminderHour = selectedHour
                            reminderManager.reminderMinute = selectedMinute
                            reminderManager.sendTestNotification()
                            Toast.makeText(context, "Тестовое уведомление отправлено!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_test_reminder"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonCyan
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(NeonCyan.copy(0.4f), NeonViolet.copy(0.4f)))
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Проверить уведомление",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Dialog Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Отмена",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            reminderManager.updateReminderSettings(
                                enabled = isEnabled,
                                hour = selectedHour,
                                minute = selectedMinute,
                                onlyPending = onlyIfPending
                            )
                            val message = if (isEnabled) {
                                String.format("Напоминание включено на %02d:%02d", selectedHour, selectedMinute)
                            } else {
                                "Напоминания отключены"
                            }
                            onSaveSuccess(message)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("btn_save_reminder"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonViolet
                        )
                    ) {
                        Text(
                            text = "Сохранить",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
