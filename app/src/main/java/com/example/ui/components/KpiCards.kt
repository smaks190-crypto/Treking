package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HabitThemeUtils
import com.example.ui.HabitUiState
import com.example.ui.theme.BgCard
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun KpiCardsSection(
    state: HabitUiState,
    modifier: Modifier = Modifier
) {
    val activeDayName = state.weekStats.getOrNull(state.activeDayIndex)?.shortName ?: "Сегодня"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Goals + Best Streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Day goals card
            KpiCardItem(
                modifier = Modifier.weight(1f),
                label = "ЦЕЛИ ($activeDayName)",
                labelColor = NeonViolet,
                value = "${state.todayPercent}%",
                fraction = "${state.todayCompletedCount}/${state.todayTotalCount}",
                subtitle = if (state.todayTotalCount == 0) "Нет целей" else if (state.todayPercent == 100) "Идеально!" else "Отличный темп",
                testTag = "kpi_today_card"
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = (state.todayPercent / 100f).coerceIn(0f, 1f),
                    animationSpec = tween(500),
                    label = "kpi_ring"
                )
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(44.dp)) {
                        val strokeW = 4.5.dp.toPx()
                        drawCircle(
                            color = Color(0xFF1E293B),
                            style = Stroke(width = strokeW)
                        )
                        drawArc(
                            brush = Brush.linearGradient(listOf(NeonViolet, NeonCyan)),
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 2. Best streak card
            KpiCardItem(
                modifier = Modifier.weight(1f),
                label = "ЛУЧШАЯ СЕРИЯ",
                labelColor = NeonOrange,
                value = "${state.bestStreak}",
                fraction = HabitThemeUtils.pluralizeDays(state.bestStreak),
                subtitle = state.bestStreakHabitName,
                testTag = "kpi_streak_card"
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonOrange.copy(alpha = 0.15f))
                        .border(1.dp, NeonOrange.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Стрик",
                        tint = NeonOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Row 2: Weekly productivity + Perfect days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 3. Weekly rate card
            KpiCardItem(
                modifier = Modifier.weight(1f),
                label = "ПРОДУКТИВНОСТЬ",
                labelColor = NeonEmerald,
                value = "${state.weeklyRatePercent}%",
                fraction = "${state.weeklyCompletedTotal}/${state.weeklyPossibleTotal}",
                subtitle = "За всю неделю",
                testTag = "kpi_weekly_card"
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonEmerald.copy(alpha = 0.15f))
                        .border(1.dp, NeonEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Продуктивность",
                        tint = NeonEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 4. Perfect days card
            KpiCardItem(
                modifier = Modifier.weight(1f),
                label = "ИДЕАЛЬНЫЕ ДНИ",
                labelColor = NeonCyan,
                value = "${state.perfectDaysCount}",
                fraction = "из 7 дней",
                subtitle = "Серии: ${state.sumStreaks} дн.",
                testTag = "kpi_perfect_card"
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Награды",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCardItem(
    modifier: Modifier = Modifier,
    label: String,
    labelColor: Color,
    value: String,
    fraction: String,
    subtitle: String,
    testTag: String,
    visualSlot: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 92.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = fraction,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                visualSlot()
            }
        }
    }
}
