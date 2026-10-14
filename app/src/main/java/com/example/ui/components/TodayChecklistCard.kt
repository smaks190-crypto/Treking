package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TodayChecklistCard(
    habits: List<HabitEntity>,
    activeDayIndex: Int,
    activeDayFullName: String,
    onToggleHabit: (String) -> Unit,
    onToggleAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remainingCount = habits.count { !it.isCompletedOnDay(activeDayIndex) }

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
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = NeonViolet,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Цели на ${activeDayFullName.lowercase()}",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Быстрая отметка выполнения привычек",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onToggleAll,
                    modifier = Modifier.testTag("mark_all_button"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NeonViolet.copy(alpha = 0.15f),
                        contentColor = NeonViolet
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Отметить все",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет привычек. Нажмите «+ Новая привычка»",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    habits.forEach { habit ->
                        val isDone = habit.isCompletedOnDay(activeDayIndex)
                        val colorDef = HabitThemeUtils.getColor(habit.color)
                        val streak = habit.calculateStreak(activeDayIndex)

                        TodayHabitItemRow(
                            habit = habit,
                            isDone = isDone,
                            colorDef = colorDef,
                            streak = streak,
                            onToggle = { onToggleHabit(habit.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Нажатие мгновенно обновляет аналитику",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = if (remainingCount == 0) "Все цели закрыты!" else "Осталось: $remainingCount",
                    color = if (remainingCount == 0) NeonViolet else TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TodayHabitItemRow(
    habit: HabitEntity,
    isDone: Boolean,
    colorDef: com.example.ui.NeonColorDefinition,
    streak: Int,
    onToggle: () -> Unit
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (isDone) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkbox_scale"
    )
    val cardBg by animateColorAsState(
        targetValue = if (isDone) Color(0xFF0F172A).copy(alpha = 0.9f) else Color(0xFF0F172A).copy(alpha = 0.4f),
        label = "habit_card_bg"
    )
    val borderCol by animateColorAsState(
        targetValue = if (isDone) BorderHighlight else BorderDark,
        label = "habit_card_border"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("checklist_item_${habit.id}")
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
            .clickable { onToggle() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .scale(scaleAnim)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDone) colorDef.primary else Color(0xFF1E293B))
                    .border(
                        1.dp,
                        if (isDone) colorDef.primary else Color(0xFF334155),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Готово",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    color = if (isDone) TextMuted else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = habit.category,
                    color = colorDef.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Flame streak badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = if (streak > 0) NeonOrange else TextMuted,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$streak",
                color = if (streak > 0) Color.White else TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
