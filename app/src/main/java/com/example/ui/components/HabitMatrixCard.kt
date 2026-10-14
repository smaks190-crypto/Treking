package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HabitEntity
import com.example.ui.HabitThemeUtils
import com.example.ui.theme.BgCard
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRose
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private val ColHabitWidth = 210.dp
private val ColDayWidth = 44.dp
private val ColWeekRateWidth = 84.dp
private val ColStreakWidth = 74.dp
private val ColActionWidth = 36.dp

@Composable
fun HabitMatrixCard(
    habits: List<HabitEntity>,
    categories: List<String>,
    selectedCategory: String,
    activeDayIndex: Int,
    isDemoActive: Boolean,
    onSelectCategory: (String) -> Unit,
    onToggleDay: (String, Int) -> Unit,
    onDeleteHabit: (String, String) -> Unit,
    onOpenAddDialog: () -> Unit,
    onRestoreDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shortDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Матрица привычек за неделю",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Нажимайте на ячейки любого дня (Пн–Вс)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) NeonViolet else Color(0xFF0F172A))
                            .border(
                                1.dp,
                                if (isSelected) NeonViolet else BorderDark,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectCategory(cat) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (habits.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Список привычек пуст",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Начните с чистого листа или загрузите примеры",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onOpenAddDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "+ Добавить привычку", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (!isDemoActive) {
                            OutlinedButton(
                                onClick = onRestoreDemo,
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderDark))
                            ) {
                                Text(text = "Загрузить примеры", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                // Horizontally scrollable Matrix table with mathematically strict column alignment
                val tableScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tableScrollState)
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ПРИВЫЧКА / КАТЕГОРИЯ",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(ColHabitWidth)
                        )

                        shortDays.forEachIndexed { dIdx, dayName ->
                            val isFocus = dIdx == activeDayIndex
                            Box(
                                modifier = Modifier.width(ColDayWidth),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayName,
                                    color = if (isFocus) NeonCyan else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isFocus) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    textDecoration = if (isFocus) TextDecoration.Underline else TextDecoration.None
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.width(ColWeekRateWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ЗА НЕДЕЛЮ",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier.width(ColStreakWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "СТРИК",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(ColActionWidth))
                    }

                    HorizontalDivider(color = BorderDark, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Table Rows with increased height and spacious padding
                    habits.forEach { habit ->
                        val colorDef = HabitThemeUtils.getColor(habit.color)
                        val iconVector = HabitThemeUtils.getIcon(habit.icon)
                        val week = habit.getWeekList()
                        val doneCount = week.count { it }
                        val weekRate = (doneCount * 100) / 7
                        val streak = habit.calculateStreak(activeDayIndex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Column 1: Habit info (width = ColHabitWidth)
                            Row(
                                modifier = Modifier.width(ColHabitWidth),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colorDef.bgSoft)
                                        .border(1.dp, colorDef.borderSoft, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = null,
                                        tint = colorDef.primary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = habit.title,
                                        color = TextPrimary,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = habit.category,
                                            color = colorDef.primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (habit.priorStreak > 0) {
                                            Text(
                                                text = " • +${habit.priorStreak} дн.",
                                                color = TextMuted,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Column 2: 7 day check buttons (each exactly ColDayWidth)
                            week.forEachIndexed { dIdx, isDone ->
                                val isFocusCol = dIdx == activeDayIndex
                                Box(
                                    modifier = Modifier.width(ColDayWidth),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isDone) colorDef.primary else if (isFocusCol) Color(0xFF1E293B) else Color(0xFF0F172A)
                                            )
                                            .border(
                                                1.dp,
                                                if (isDone) colorDef.primary else if (isFocusCol) NeonCyan.copy(alpha = 0.5f) else BorderDark,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { onToggleDay(habit.id, dIdx) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Выполнено",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Column 3: Week rate (ColWeekRateWidth)
                            Column(
                                modifier = Modifier
                                    .width(ColWeekRateWidth)
                                    .padding(horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$doneCount/7 ($weekRate%)",
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                LinearProgressIndicator(
                                    progress = { weekRate / 100f },
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = colorDef.primary,
                                    trackColor = Color(0xFF1E293B)
                                )
                            }

                            // Column 4: Streak badge (ColStreakWidth)
                            Box(
                                modifier = Modifier.width(ColStreakWidth),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (streak > 0) NeonOrange.copy(alpha = 0.15f) else Color(0xFF1E293B))
                                        .border(
                                            1.dp,
                                            if (streak > 0) NeonOrange.copy(alpha = 0.35f) else BorderDark,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = if (streak > 0) NeonOrange else TextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "$streak",
                                        color = if (streak > 0) Color.White else TextMuted,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Column 5: Delete button (ColActionWidth)
                            Box(
                                modifier = Modifier.width(ColActionWidth),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { onDeleteHabit(habit.id, habit.title) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Удалить",
                                        tint = NeonRose.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
