package com.example.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DragonGreen
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun Top5Card(
    top5Tasks: Map<Int, TaskEntity>, // index 0..4 -> task
    onSlotClicked: (slotIndex: Int) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onRemoveFromTop5: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    DashedBorderCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("my_top_5_card"),
        borderColor = ElectricPink.copy(alpha = 0.7f),
        backdropRes = com.example.R.drawable.img_graffiti_top5_beasts_1790916951243
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ElectricPink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Top 5 Star",
                            tint = NeonYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "My Top 5",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Daily non-negotiable priorities",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                val completedTop5 = top5Tasks.values.count { it.isCompleted }
                val totalTop5 = top5Tasks.size
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (totalTop5 > 0 && completedTop5 == totalTop5) DragonGreen.copy(alpha = 0.25f) else Color(0x33FF007F))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$completedTop5 / 5 done",
                        color = if (totalTop5 > 0 && completedTop5 == totalTop5) DragonGreen else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 5 Slots
            for (i in 0 until 5) {
                val assignedTask = top5Tasks[i]
                if (assignedTask != null) {
                    // Render Assigned Task Slot
                    Top5AssignedSlot(
                        slotNumber = i + 1,
                        task = assignedTask,
                        onToggle = { onToggleTask(assignedTask) },
                        onRemove = { onRemoveFromTop5(assignedTask) }
                    )
                } else {
                    // Render Empty Slot Button
                    Top5EmptySlot(
                        slotNumber = i + 1,
                        onClick = { onSlotClicked(i) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Top5AssignedSlot(
    slotNumber: Int,
    task: TaskEntity,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    val timeSlotEnum = TimeSlot.fromString(task.slot)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (task.isCompleted) Color(0x1F00E676) else Color(0x22111D3B))
            .border(
                width = 1.dp,
                color = if (task.isCompleted) DragonGreen.copy(alpha = 0.4f) else ElectricPink.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("top_5_slot_$slotNumber"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Slot Number Badge
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (task.isCompleted) DragonGreen.copy(alpha = 0.3f) else ElectricPink.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$slotNumber",
                color = if (task.isCompleted) DragonGreen else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Checkbox icon
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (task.isCompleted) DragonGreen else Color(0x33FFFFFF))
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

        // Title and Slot tag
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = if (task.isCompleted) TextMuted else TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${timeSlotEnum.icon} ${timeSlotEnum.displayName}",
                    color = CyberCyan,
                    fontSize = 11.sp
                )
                val timeFormatted = task.getFormattedTime()
                if (timeFormatted.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (task.isCompleted) Color(0x15FFFFFF)
                                else CyberCyan.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 5.dp, vertical = 1.dp)
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

        // Remove from Top 5 button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove from Top 5",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun Top5EmptySlot(
    slotNumber: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x10FF007F))
            .border(
                width = 1.dp,
                color = ElectricPink.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("pick_top_5_slot_$slotNumber"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add slot $slotNumber",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "+ Pick Top 5 task $slotNumber",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
