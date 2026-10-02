package com.example.ui.components

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.core.content.ContextCompat
import com.example.data.local.TaskEntity
import com.example.data.local.TimeSlot
import com.example.notification.SoundPreviewHelper
import com.example.notification.TaskNotificationHelper
import com.example.ui.theme.CardGlowBorder
import com.example.ui.theme.CyberBorderStroke
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberInputBg
import com.example.ui.theme.DragonGreen
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

@Composable
fun AddTaskDialog(
    initialSlot: TimeSlot = TimeSlot.MORNING,
    initialIsTop5: Boolean = false,
    initialTop5SlotIndex: Int = -1,
    initialScheduledTime: String = "",
    initialScheduledTimeMillis: Long? = null,
    selectedDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        slot: TimeSlot,
        isTop5: Boolean,
        top5SlotIndex: Int,
        scheduledTime: String,
        scheduledTimeMillis: Long?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf(initialSlot) }
    var isTop5 by remember { mutableStateOf(initialIsTop5) }
    var scheduledTime by remember { mutableStateOf(initialScheduledTime) }
    var scheduledTimeMillis by remember { mutableStateOf(initialScheduledTimeMillis) }
    var isPlayingPreview by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Initialize notification channel
    remember {
        TaskNotificationHelper.createNotificationChannel(context)
    }

    // Permission launcher for Android 13+ (POST_NOTIFICATIONS)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            SoundPreviewHelper.stop()
        }
    }

    // Compute formatted reminder text
    val reminderDisplayString: String = remember(scheduledTimeMillis, scheduledTime) {
        if (scheduledTimeMillis != null) {
            val cal = Calendar.getInstance().apply { timeInMillis = scheduledTimeMillis!! }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val timeStr = String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
            val calToday = Calendar.getInstance()
            val isToday = cal.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)
            if (isToday) "Today at $timeStr" else "${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)} at $timeStr"
        } else if (scheduledTime.isNotBlank()) {
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
                String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
            } catch (e: Exception) {
                scheduledTime
            }
        } else {
            "Set exact date & time reminder"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CyberCardBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CardGlowBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
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

                // Scheduled Notification Reminder (Date + Time Picker)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = if (scheduledTimeMillis != null) DragonGreen else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Scheduled Reminder (Exact Alarm)",
                                color = if (scheduledTimeMillis != null) DragonGreen else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (scheduledTimeMillis != null || scheduledTime.isNotBlank()) {
                            Text(
                                text = "Clear",
                                color = ElectricPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        scheduledTime = ""
                                        scheduledTimeMillis = null
                                    }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Date & Time Picker trigger
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (scheduledTimeMillis != null) CyberCyan.copy(alpha = 0.15f) else CyberInputBg)
                            .border(
                                width = 1.dp,
                                color = if (scheduledTimeMillis != null) CyberCyan else CyberBorderStroke,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                checkAndRequestNotificationPermission()

                                // Open DatePicker first, then TimePicker
                                val cal = Calendar.getInstance()
                                if (scheduledTimeMillis != null) {
                                    cal.timeInMillis = scheduledTimeMillis!!
                                } else {
                                    cal.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
                                    val defaultHour = when (selectedSlot) {
                                        TimeSlot.MORNING -> 7
                                        TimeSlot.AFTERNOON -> 13
                                        TimeSlot.EVENING -> 18
                                        TimeSlot.NIGHT -> 21
                                    }
                                    cal.set(Calendar.HOUR_OF_DAY, defaultHour)
                                    cal.set(Calendar.MINUTE, 0)
                                    cal.set(Calendar.SECOND, 0)
                                    cal.set(Calendar.MILLISECOND, 0)
                                }

                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        cal.set(Calendar.YEAR, year)
                                        cal.set(Calendar.MONTH, month)
                                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                        // Now open TimePicker
                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                cal.set(Calendar.MINUTE, minute)
                                                cal.set(Calendar.SECOND, 0)
                                                cal.set(Calendar.MILLISECOND, 0)

                                                scheduledTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                                                scheduledTimeMillis = cal.timeInMillis
                                            },
                                            cal.get(Calendar.HOUR_OF_DAY),
                                            cal.get(Calendar.MINUTE),
                                            false // 12-hour AM/PM dialog
                                        ).show()
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
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
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Notification",
                                    tint = if (scheduledTimeMillis != null) CyberCyan else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = reminderDisplayString,
                                    color = if (scheduledTimeMillis != null) CyberCyan else TextMuted.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    fontWeight = if (scheduledTimeMillis != null) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            if (scheduledTimeMillis != null) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFFFFF))
                                        .clickable {
                                            scheduledTime = ""
                                            scheduledTimeMillis = null
                                        },
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

                // Preview Dragon Chime Section (Compact Circular Audio Button)
                val previewCardBorderColor by animateColorAsState(
                    targetValue = if (isPlayingPreview) DragonGreen else CyberBorderStroke,
                    label = "previewBorder"
                )
                val previewCardBg by animateColorAsState(
                    targetValue = if (isPlayingPreview) DragonGreen.copy(alpha = 0.12f) else Color(0x1800F0FF),
                    label = "previewBg"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(previewCardBg)
                        .border(1.dp, previewCardBorderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Icon in accent circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isPlayingPreview) DragonGreen.copy(alpha = 0.25f) else CyberCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Chime Preview",
                            tint = if (isPlayingPreview) DragonGreen else CyberCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Middle Text Details (Flexible weight, strictly single-line)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isPlayingPreview) "Playing Marimba Chime..." else "Notification Chime",
                            color = if (isPlayingPreview) DragonGreen else TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Soft buoyant marimba bounce",
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Right: Compact Circular Audio Icon Button (Zero text wrapping)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPlayingPreview) DragonGreen
                                else CyberCyan.copy(alpha = 0.18f)
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isPlayingPreview) DragonGreen else CyberCyan,
                                shape = CircleShape
                            )
                            .clickable {
                                if (isPlayingPreview) {
                                    SoundPreviewHelper.stop()
                                    isPlayingPreview = false
                                } else {
                                    isPlayingPreview = true
                                    SoundPreviewHelper.playDragonChime(context) {
                                        isPlayingPreview = false
                                    }
                                }
                            }
                            .testTag("preview_dragon_chime_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlayingPreview) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlayingPreview) "Stop audio" else "Play marimba chime",
                            tint = if (isPlayingPreview) Color.Black else CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
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
                    TextButton(onClick = {
                        SoundPreviewHelper.stop()
                        onDismiss()
                    }) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                SoundPreviewHelper.stop()
                                onConfirm(
                                    title.trim(),
                                    selectedSlot,
                                    isTop5,
                                    initialTop5SlotIndex,
                                    scheduledTime,
                                    scheduledTimeMillis
                                )
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
                                        val icon = if (task.scheduledTimeMillis != null) "🔔" else "⏰"
                                        Text(
                                            text = "$icon $timeStr",
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
