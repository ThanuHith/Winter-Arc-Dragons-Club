package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskEntity
import com.example.data.local.TimeSlot
import com.example.ui.theme.CyberBorderStroke
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DragonGreen
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun RoutineSectionCard(
    slot: TimeSlot,
    tasks: List<TaskEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onToggleTop5: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onAddTaskInSlot: (TimeSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size

    val slotBackdrop = when (slot) {
        TimeSlot.MORNING -> com.example.R.drawable.img_graffiti_morning_dragon_1790916891685
        TimeSlot.AFTERNOON -> com.example.R.drawable.img_graffiti_afternoon_panther_1790916905134
        TimeSlot.EVENING -> com.example.R.drawable.img_graffiti_evening_dragon_1790916922726
        TimeSlot.NIGHT -> com.example.R.drawable.img_graffiti_night_panther_1790916936881
    }

    val slotGlowBrush = when (slot) {
        TimeSlot.MORNING -> com.example.ui.theme.MorningGlowBorder
        TimeSlot.AFTERNOON -> com.example.ui.theme.AfternoonGlowBorder
        TimeSlot.EVENING -> com.example.ui.theme.EveningGlowBorder
        TimeSlot.NIGHT -> com.example.ui.theme.NightGlowBorder
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("routine_card_${slot.name.lowercase()}"),
        cornerRadius = 16.dp,
        backdropRes = slotBackdrop,
        hasGradientGlow = true,
        glowBrush = slotGlowBrush
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = slot.icon,
                        fontSize = 20.sp
                    )
                    Text(
                        text = slot.displayName,
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (totalCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (completedCount == totalCount) DragonGreen.copy(alpha = 0.2f)
                                    else CyberCyan.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$completedCount/$totalCount done",
                                color = if (completedCount == totalCount) DragonGreen else CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Mini Add Button in Header
                    IconButton(
                        onClick = { onAddTaskInSlot(slot) },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("add_task_${slot.name.lowercase()}_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add task in ${slot.displayName}",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Task list
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x0AFFFFFF))
                        .clickable { onAddTaskInSlot(slot) }
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "+ Add ${slot.displayName.lowercase()} routine...",
                        color = TextMuted.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            } else {
                tasks.forEach { task ->
                    TaskItemRow(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onToggleTop5 = { onToggleTop5(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onToggleTop5: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (task.isCompleted) Color(0x1A00E676) else Color(0x1F111D3B))
            .border(
                width = 1.dp,
                color = if (task.isCompleted) DragonGreen.copy(alpha = 0.35f) else CyberBorderStroke.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = 9.dp)
            .testTag("task_item_${task.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (task.isCompleted) DragonGreen else Color(0x22FFFFFF))
                .border(
                    width = 1.dp,
                    color = if (task.isCompleted) DragonGreen else Color(0x66FFFFFF),
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Title and Time
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = if (task.isCompleted) TextMuted else TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            val timeFormatted = task.getFormattedTime()
            if (timeFormatted.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (task.isCompleted) Color(0x15FFFFFF)
                                else CyberCyan.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⏰ $timeFormatted",
                            color = if (task.isCompleted) TextMuted else CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Top 5 Star Toggle
        IconButton(
            onClick = onToggleTop5,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = if (task.isTop5) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = if (task.isTop5) "In Top 5" else "Add to Top 5",
                tint = if (task.isTop5) NeonYellow else TextMuted.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete task",
                tint = TextMuted.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
