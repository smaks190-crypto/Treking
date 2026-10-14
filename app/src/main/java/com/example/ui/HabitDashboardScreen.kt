package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ActiveDaySelectorBar
import com.example.ui.components.AddHabitDialog
import com.example.ui.components.HabitMatrixCard
import com.example.ui.components.KpiCardsSection
import com.example.ui.components.ReminderSettingsDialog
import com.example.ui.components.ResetConfirmDialog
import com.example.ui.components.TodayChecklistCard
import com.example.ui.components.WeeklyChartCard
import com.example.ui.theme.BgMain
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HabitDashboardScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-clear toast after 2.5 seconds
    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(2500)
            viewModel.clearToast()
        }
    }

    val formattedDate = remember(state.activeDayIndex) {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
        val formatted = dateFormat.format(Date())
        val capitalized = formatted.replaceFirstChar { it.uppercase() }
        val activeName = state.weekStats.getOrNull(state.activeDayIndex)?.fullName ?: "Сегодня"
        "$capitalized • Фокус: $activeName"
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BgMain),
        containerColor = BgMain
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo and Title
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    Brush.linearGradient(listOf(NeonViolet, Color(0xFF4F46E5), NeonCyan))
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PulseHabit",
                                    color = TextPrimary,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonViolet.copy(alpha = 0.2f))
                                        .border(
                                            1.dp,
                                            NeonViolet.copy(alpha = 0.4f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        color = NeonViolet,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = formattedDate,
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Compact Top Action Buttons (balanced, proportional)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reminder Settings button
                        Box(
                            modifier = Modifier
                                .testTag("reminder_button")
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (state.isReminderActive) NeonViolet.copy(alpha = 0.25f)
                                    else Color(0xFF1E293B)
                                )
                                .border(
                                    1.dp,
                                    if (state.isReminderActive) NeonViolet else BorderDark,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setShowReminderDialog(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (state.isReminderActive) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Напоминания",
                                tint = if (state.isReminderActive) NeonCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Reset / Demo toggle button
                        Box(
                            modifier = Modifier
                                .testTag("reset_demo_button")
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                                .clickable { viewModel.onResetClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Сброс демо-данных",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Add Habit compact button
                        Box(
                            modifier = Modifier
                                .testTag("add_habit_button")
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonViolet)
                                .clickable { viewModel.setShowAddDialog(true) }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Добавить",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Новая",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Demo Mode Indicator Banner
                if (state.isDemoActive) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚠️", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Демо-режим (показаны примеры)",
                            color = Color(0xFFFDE68A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Active Day Selector
                ActiveDaySelectorBar(
                    activeDayIndex = state.activeDayIndex,
                    realTodayIndex = state.realTodayIndex,
                    onSelectDay = { viewModel.setActiveDay(it) }
                )

                // 4 KPI Cards (2x2 grid, unclipped labels, uniform right visual slot)
                KpiCardsSection(state = state)

                // Today checklist
                TodayChecklistCard(
                    habits = state.habits,
                    activeDayIndex = state.activeDayIndex,
                    activeDayFullName = state.weekStats.getOrNull(state.activeDayIndex)?.fullName ?: "Сегодня",
                    onToggleHabit = { habitId -> viewModel.toggleHabitDay(habitId, state.activeDayIndex) },
                    onToggleAll = { viewModel.toggleAllForActiveDay() }
                )

                // Weekly Chart (Bar / Wave)
                WeeklyChartCard(
                    weekStats = state.weekStats,
                    activeDayIndex = state.activeDayIndex,
                    chartMode = state.chartMode,
                    bestDaySummary = state.bestDaySummary,
                    averagePercent = state.averageDailyPercent,
                    onSelectDay = { viewModel.setActiveDay(it) },
                    onSetChartMode = { viewModel.setChartMode(it) }
                )

                // Habit Matrix Table
                HabitMatrixCard(
                    habits = state.filteredHabits,
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    activeDayIndex = state.activeDayIndex,
                    isDemoActive = state.isDemoActive,
                    onSelectCategory = { viewModel.setCategory(it) },
                    onToggleDay = { habitId, dIdx -> viewModel.toggleHabitDay(habitId, dIdx) },
                    onDeleteHabit = { habitId, title -> viewModel.deleteHabit(habitId, title) },
                    onOpenAddDialog = { viewModel.setShowAddDialog(true) },
                    onRestoreDemo = { viewModel.restoreDemoMode() }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Floating Toast notification
            AnimatedVisibility(
                visible = state.toastMessage != null,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                state.toastMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E1B4B))
                            .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Add Habit Dialog
            if (state.showAddDialog) {
                AddHabitDialog(
                    onDismiss = { viewModel.setShowAddDialog(false) },
                    onSave = { title, cat, icon, color, prior ->
                        viewModel.addHabit(title, cat, icon, color, prior)
                    }
                )
            }

            // Reset Confirmation Dialog
            if (state.showResetConfirmDialog) {
                ResetConfirmDialog(
                    isDemoActive = state.isDemoActive,
                    hasHabits = state.habits.isNotEmpty(),
                    onDismiss = { viewModel.dismissResetConfirmDialog() },
                    onConfirm = { viewModel.confirmExitDemoMode() }
                )
            }

            // Reminder Settings Dialog
            if (state.showReminderDialog) {
                ReminderSettingsDialog(
                    reminderManager = viewModel.reminderManager,
                    onDismiss = { viewModel.setShowReminderDialog(false) },
                    onSaveSuccess = { message ->
                        viewModel.onReminderSaved(message)
                    }
                )
            }
        }
    }
}
