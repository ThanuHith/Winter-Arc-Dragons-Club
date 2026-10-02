package com.example.ui.components

import android.app.TimePickerDialog
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.TaskEntity
import com.example.data.local.TimeSlot
import com.example.ui.theme.CyberBorderStroke
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberInputBg
import com.example.ui.theme.DragonGreen
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun AddTaskDialog(
    initialSlot: TimeSlot = TimeSlot.MORNING,
    initialIsTop5: Boolean = false,
    initialTop5SlotIndex: Int = -1,
    initialScheduledTime: String = "",
    onDismiss: () -> Unit,
    onConfirm: (title: String, slot: TimeSlot, isTop5: Boolean, top5SlotIndex: Int, scheduledTime: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf(initialSlot) }
    var isTop5 by remember { mutableStateOf(initialIsTop5) }
    var scheduledTime by remember { mutableStateOf(initialScheduledTime) }
    val context = LocalContext.current

    val formattedTimeDisplay = if (scheduledTime.isNotBlank()) {
        try {
            val parts = scheduledTime.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format("%d:%02d %s", displayHour, minute, amPm)
        } catch (e: Exception) {
            scheduledTime
        }
    } else "Set time (AM/PM)"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CyberCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderStroke),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialTop5SlotIndex >= 0) "Add Top 5 Mission" else "Add Winter Arc Task",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                // Task Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g., Cold Shower & 50 Pushups", color = TextMuted.copy(alpha = 0.6f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = CyberInputBg,
                        unfocusedContainerColor = CyberInputBg,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorderStroke,
                        cursorColor = CyberCyan
                    ),
                    singleLine = false,
                    maxLines = 3
                )

                // Time Slot Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Time of Day",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TimeSlot.entries.forEach { slot ->
                            val isSelected = slot == selectedSlot
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) CyberCyan.copy(alpha = 0.25f)
                                        else Color(0x15FFFFFF)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CyberCyan else Color(0x22FFFFFF),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedSlot = slot }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = slot.icon, fontSize = 16.sp)
                                    Text(
                                        text = slot.displayName,
                                        color = if (isSelected) CyberCyan else TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Scheduled Time Selector (AM / PM Native Dialog)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Scheduled Time (Optional)",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (scheduledTime.isNotBlank()) {
                            Text(
                                text = "Clear",
                                color = TextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.clickable { scheduledTime = "" }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (scheduledTime.isNotBlank()) CyberCyan.copy(alpha = 0.15f) else CyberInputBg)
                            .border(
                                width = 1.dp,
                                color = if (scheduledTime.isNotBlank()) CyberCyan else CyberBorderStroke,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                var curHour = 7
                                var curMinute = 0
                                if (scheduledTime.isNotBlank()) {
                                    try {
                                        val parts = scheduledTime.split(":")
                                        curHour = parts[0].toInt()
                                        curMinute = parts[1].toInt()
                                    } catch (_: Exception) {}
                                } else {
                                    curHour = when (selectedSlot) {
                                        TimeSlot.MORNING -> 7
                                        TimeSlot.AFTERNOON -> 13
                                        TimeSlot.EVENING -> 18
                                        TimeSlot.NIGHT -> 21
                                    }
                                }
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        scheduledTime = String.format("%02d:%02d", hourOfDay, minute)
                                    },
                                    curHour,
                                    curMinute,
                                    false // 12-hour AM/PM dialog
                                ).show()
                            }
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                            .testTag("time_picker_button")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Time",
                                    tint = if (scheduledTime.isNotBlank()) CyberCyan else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = formattedTimeDisplay,
                                    color = if (scheduledTime.isNotBlank()) CyberCyan else TextMuted.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    fontWeight = if (scheduledTime.isNotBlank()) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            if (scheduledTime.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFFFFF))
                                        .clickable { scheduledTime = "" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear time",
                                        tint = TextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Add to Top 5 toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isTop5) ElectricPink.copy(alpha = 0.15f) else Color(0x0FFFFFFF))
                        .clickable { isTop5 = !isTop5 }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isTop5,
                        onCheckedChange = { isTop5 = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = ElectricPink,
                            checkmarkColor = Color.White,
                            uncheckedColor = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Mark as My Top 5 Priority",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = NeonYellow,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "Pin to your daily non-negotiable Top 5 board",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title.trim(), selectedSlot, isTop5, initialTop5SlotIndex, scheduledTime)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DragonGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_task_button")
                    ) {
                        Text(
                            text = "Save Task",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PickTop5TaskDialog(
    slotIndex: Int,
    availableTasks: List<TaskEntity>,
    onDismiss: () -> Unit,
    onAssignExisting: (TaskEntity, slotIndex: Int) -> Unit,
    onCreateNew: (slotIndex: Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CyberCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPink.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pick Top 5 Task #${slotIndex + 1}",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose an existing task or create a new priority",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                // Create new priority button
                Button(
                    onClick = {
                        onDismiss()
                        onCreateNew(slotIndex)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_new_top5_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricPink.copy(alpha = 0.2f),
                        contentColor = ElectricPink
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPink)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = ElectricPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+ Write New Priority Task",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (availableTasks.isNotEmpty()) {
                    Text(
                        text = "Or choose from today's schedule:",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableTasks) { task ->
                            val slotEnum = TimeSlot.fromString(task.slot)
                            val timeStr = task.getFormattedTime()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x18111D3B))
                                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                                    .clickable {
                                        onAssignExisting(task, slotIndex)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = slotEnum.icon,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    if (timeStr.isNotBlank()) {
                                        Text(
                                            text = "⏰ $timeStr",
                                            color = CyberCyan,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Text(
                                    text = "Assign",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
