package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BgCard
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ResetConfirmDialog(
    isDemoActive: Boolean,
    hasHabits: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val title = when {
        isDemoActive -> "Выйти из демо-режима?"
        hasHabits -> "Сбросить все привычки?"
        else -> "Восстановить демо-режим?"
    }

    val subtitle = when {
        isDemoActive -> "Очистка демонстрационных данных"
        hasHabits -> "Очистка созданных данных"
        else -> "Загрузка примеров"
    }

    val message = when {
        isDemoActive -> "Все текущие демонстрационные данные будут удалены, и вы сможете начать вести привычки с чистого листа."
        hasHabits -> "Все созданные вами привычки и история выполнения будут удалены безвозвратно. Начать с чистого листа?"
        else -> "Загрузить демонстрационный набор привычек и недельную статистику для ознакомления?"
    }

    val confirmText = when {
        isDemoActive -> "Выйти и очистить"
        hasHabits -> "Сбросить всё"
        else -> "Загрузить демо"
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(BgCard)
                .border(1.dp, BorderHighlight, RoundedCornerShape(22.dp))
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header icon & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonRose.copy(alpha = 0.15f))
                            .border(1.dp, NeonRose.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = NeonRose,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Отмена", color = TextSecondary, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.testTag("confirm_exit_demo_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRose),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = confirmText,
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
