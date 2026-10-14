package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRose
import com.example.ui.theme.NeonViolet

data class NeonColorDefinition(
    val key: String,
    val name: String,
    val primary: Color,
    val bgSoft: Color,
    val borderSoft: Color
)

object HabitThemeUtils {
    val COLORS = mapOf(
        "violet" to NeonColorDefinition(
            key = "violet",
            name = "Фиолетовый неон",
            primary = NeonViolet,
            bgSoft = NeonViolet.copy(alpha = 0.15f),
            borderSoft = NeonViolet.copy(alpha = 0.35f)
        ),
        "cyan" to NeonColorDefinition(
            key = "cyan",
            name = "Кибер-циан",
            primary = NeonCyan,
            bgSoft = NeonCyan.copy(alpha = 0.15f),
            borderSoft = NeonCyan.copy(alpha = 0.35f)
        ),
        "emerald" to NeonColorDefinition(
            key = "emerald",
            name = "Изумрудный",
            primary = NeonEmerald,
            bgSoft = NeonEmerald.copy(alpha = 0.15f),
            borderSoft = NeonEmerald.copy(alpha = 0.35f)
        ),
        "rose" to NeonColorDefinition(
            key = "rose",
            name = "Неоновый розовый",
            primary = NeonRose,
            bgSoft = NeonRose.copy(alpha = 0.15f),
            borderSoft = NeonRose.copy(alpha = 0.35f)
        ),
        "amber" to NeonColorDefinition(
            key = "amber",
            name = "Янтарный",
            primary = NeonAmber,
            bgSoft = NeonAmber.copy(alpha = 0.15f),
            borderSoft = NeonAmber.copy(alpha = 0.35f)
        ),
        "blue" to NeonColorDefinition(
            key = "blue",
            name = "Электрик синий",
            primary = NeonBlue,
            bgSoft = NeonBlue.copy(alpha = 0.15f),
            borderSoft = NeonBlue.copy(alpha = 0.35f)
        )
    )

    fun getColor(key: String): NeonColorDefinition {
        return COLORS[key] ?: COLORS["violet"]!!
    }

    val ICONS = listOf(
        "code-2" to Icons.Default.Code,
        "music-4" to Icons.Default.MusicNote,
        "book-open" to Icons.Default.MenuBook,
        "dumbbell" to Icons.Default.FitnessCenter,
        "shield-ban" to Icons.Default.Security,
        "flame" to Icons.Default.LocalFireDepartment,
        "terminal" to Icons.Default.Terminal,
        "headphones" to Icons.Default.Headphones,
        "brain" to Icons.Default.Psychology,
        "droplets" to Icons.Default.WaterDrop,
        "moon" to Icons.Default.Nightlight,
        "zap" to Icons.Default.Bolt
    )

    fun getIcon(key: String): ImageVector {
        return ICONS.firstOrNull { it.first == key }?.second ?: Icons.Default.Code
    }

    fun pluralizeDays(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        return when {
            mod10 == 1 && mod100 != 11 -> "день"
            mod10 in 2..4 && (mod100 < 12 || mod100 > 14) -> "дня"
            else -> "дней"
        }
    }
}
