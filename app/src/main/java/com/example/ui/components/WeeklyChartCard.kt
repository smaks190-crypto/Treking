package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChartMode
import com.example.ui.DayStat
import com.example.ui.theme.BgCard
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WeeklyChartCard(
    weekStats: List<DayStat>,
    activeDayIndex: Int,
    chartMode: ChartMode,
    bestDaySummary: String,
    averagePercent: Int,
    onSelectDay: (Int) -> Unit,
    onSetChartMode: (ChartMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            // Header with Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Динамика недели (Пн–Вс)",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Процент и количество закрытых целей",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Switcher pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    // Bar button
                    Box(
                        modifier = Modifier
                            .testTag("chart_mode_bar")
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chartMode == ChartMode.BAR) NeonViolet else Color.Transparent)
                            .clickable { onSetChartMode(ChartMode.BAR) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Столбцы",
                                tint = if (chartMode == ChartMode.BAR) Color.White else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Столбцы",
                                color = if (chartMode == ChartMode.BAR) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Wave button
                    Box(
                        modifier = Modifier
                            .testTag("chart_mode_wave")
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chartMode == ChartMode.WAVE) NeonViolet else Color.Transparent)
                            .clickable { onSetChartMode(ChartMode.WAVE) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = "Волна",
                                tint = if (chartMode == ChartMode.WAVE) Color.White else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Волна",
                                color = if (chartMode == ChartMode.WAVE) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Canvas area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Dashed horizontal guidelines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val steps = 4
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    for (i in 0..steps) {
                        val y = size.height * (i.toFloat() / steps)
                        drawLine(
                            color = Color(0xFF1E293B).copy(alpha = 0.6f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = pathEffect
                        )
                    }
                }

                Crossfade(targetState = chartMode, label = "chart_crossfade") { mode ->
                    when (mode) {
                        ChartMode.BAR -> BarChartView(
                            weekStats = weekStats,
                            activeDayIndex = activeDayIndex,
                            onSelectDay = onSelectDay
                        )
                        ChartMode.WAVE -> WaveChartView(
                            weekStats = weekStats,
                            activeDayIndex = activeDayIndex,
                            onSelectDay = onSelectDay
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = bestDaySummary,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "Средний результат: $averagePercent% в день",
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun BarChartView(
    weekStats: List<DayStat>,
    activeDayIndex: Int,
    onSelectDay: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        weekStats.forEach { stat ->
            val isSelected = stat.dayIndex == activeDayIndex
            val heightFraction by animateFloatAsState(
                targetValue = (stat.percent / 100f).coerceIn(0.06f, 1f),
                animationSpec = tween(450),
                label = "bar_height"
            )

            val barBrush = if (stat.percent == 100) {
                Brush.verticalGradient(listOf(NeonEmerald, NeonCyan))
            } else if (isSelected) {
                Brush.verticalGradient(listOf(NeonCyan, NeonViolet))
            } else {
                Brush.verticalGradient(
                    listOf(
                        NeonViolet.copy(alpha = 0.7f),
                        Color(0xFF6366F1).copy(alpha = 0.6f)
                    )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSelectDay(stat.dayIndex) }
                    .padding(horizontal = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "${stat.percent}%",
                    color = if (isSelected) NeonCyan else TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stat.completedCount}/${stat.totalCount}",
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Bar track & fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan.copy(alpha = 0.5f) else BorderDark,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(heightFraction)
                            .clip(RoundedCornerShape(6.dp))
                            .background(barBrush)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stat.shortName,
                    color = if (isSelected) Color.White else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun WaveChartView(
    weekStats: List<DayStat>,
    activeDayIndex: Int,
    onSelectDay: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (weekStats.size < 2) return@Canvas

            val w = size.width
            val h = size.height
            val padX = 28.dp.toPx()
            val stepX = (w - padX * 2) / (weekStats.size - 1)

            val points = weekStats.mapIndexed { i, stat ->
                val x = padX + i * stepX
                val y = h - (stat.percent / 100f) * (h - 32.dp.toPx()) - 16.dp.toPx()
                Offset(x, y)
            }

            // Build smooth Bezier path
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val cx1 = p0.x + (p1.x - p0.x) / 2
                    val cy1 = p0.y
                    val cx2 = p0.x + (p1.x - p0.x) / 2
                    val cy2 = p1.y
                    cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                }
            }

            val areaPath = Path().apply {
                addPath(strokePath)
                lineTo(points.last().x, h)
                lineTo(points.first().x, h)
                close()
            }

            // Draw area gradient
            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    listOf(
                        NeonCyan.copy(alpha = 0.35f),
                        NeonViolet.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )

            // Draw wave line
            drawPath(
                path = strokePath,
                brush = Brush.horizontalGradient(listOf(NeonViolet, NeonCyan, NeonEmerald)),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw node circles
            points.forEachIndexed { idx, pt ->
                val isSelected = idx == activeDayIndex
                drawCircle(
                    color = Color(0xFF090D16),
                    radius = if (isSelected) 8.dp.toPx() else 5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = if (isSelected) NeonCyan else NeonViolet,
                    radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                    center = pt
                )
            }
        }

        // Day labels below the wave
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekStats.forEach { stat ->
                val isSelected = stat.dayIndex == activeDayIndex
                Column(
                    modifier = Modifier
                        .clickable { onSelectDay(stat.dayIndex) }
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${stat.percent}%",
                        color = if (isSelected) NeonCyan else TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stat.shortName,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
