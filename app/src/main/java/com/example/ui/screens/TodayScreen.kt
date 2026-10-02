package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.TimeSlot
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.NeonProgressBar
import com.example.ui.components.PickTop5TaskDialog
import com.example.ui.components.RoutineSectionCard
import com.example.ui.components.Top5Card
import com.example.ui.theme.CyberBorderStroke
import com.example.ui.theme.CyberCardBgTranslucent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DragonGreen
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.WinterArcViewModel
import java.time.LocalDate

@Composable
fun TodayScreen(
    viewModel: WinterArcViewModel,
    onNavigateToArc: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.todayUiState.collectAsStateWithLifecycle()
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val todayDate = remember { LocalDate.now() }
    val dec31 = remember(todayDate) { LocalDate.of(todayDate.year, 12, 31) }
    val daysToDec31 = remember(todayDate, dec31) {
        val diff = java.time.temporal.ChronoUnit.DAYS.between(todayDate, dec31)
        if (diff >= 0) diff else java.time.temporal.ChronoUnit.DAYS.between(todayDate, LocalDate.of(todayDate.year + 1, 12, 31))
    }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var dialogInitialSlot by remember { mutableStateOf(TimeSlot.MORNING) }
    var dialogInitialIsTop5 by remember { mutableStateOf(false) }
    var dialogTop5SlotIndex by remember { mutableStateOf(-1) }

    var showPickTop5Dialog by remember { mutableStateOf(false) }
    var selectedTop5SlotIndex by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("today_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Dynamic Streak & Dynamic Days to Dec 31
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DragonGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = DragonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Winter Arc",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite,
                                letterSpacing = 1.sp
                            )
                        }

                        // Real Dynamic Streak
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xDD0B1226))
                                .border(1.dp, ElectricPink.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🔥 ${streak}D STREAK",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dragons Club · Your rules. Your schedule.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )

                        // Real Dynamic Days to Dec 31
                        Text(
                            text = "⏳ $daysToDec31 DAYS TO DEC 31",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }
                }
            }

            // Date Switcher Bar
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 14.dp,
                    hasGradientGlow = true,
                    glowBrush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(CyberCyan, DragonGreen)),
                    backgroundColor = CyberCardBgTranslucent
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.navigateDate(-1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("prev_date_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Day",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        val cur = uiState.selectedDate
                                        DatePickerDialog(
                                            context,
                                            { _, year, month, dayOfMonth ->
                                                viewModel.setDate(LocalDate.of(year, month + 1, dayOfMonth))
                                            },
                                            cur.year,
                                            cur.monthValue - 1,
                                            cur.dayOfMonth
                                        ).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Date Picker",
                                    tint = DragonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = uiState.displayDateString,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.navigateDate(1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("next_date_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Day",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "Set your dates in the Arc tab",
                            color = CyberCyan.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable(onClick = onNavigateToArc)
                                .padding(top = 4.dp)
                        )
                    }
                }
            }

            // Progress Card
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    hasGradientGlow = true,
                    glowBrush = com.example.ui.theme.ProgressGlowBorder,
                    backdropRes = com.example.R.drawable.img_graffiti_progress_dragon_1790916963843
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val percentage = (uiState.progress * 100).toInt()
                                Text(
                                    text = "$percentage%",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (percentage == 100 && uiState.totalCount > 0) DragonGreen else TextWhite
                                )
                                Text(
                                    text = "Daily Completion",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x1F00E676))
                                    .border(1.dp, DragonGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = DragonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${uiState.completedCount} of ${uiState.totalCount} done",
                                        color = DragonGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        NeonProgressBar(
                            progress = uiState.progress,
                            height = 8.dp
                        )
                    }
                }
            }

            // My Top 5 Priority Card
            item {
                Top5Card(
                    top5Tasks = uiState.top5Map,
                    onSlotClicked = { slotIdx ->
                        selectedTop5SlotIndex = slotIdx
                        showPickTop5Dialog = true
                    },
                    onToggleTask = { viewModel.toggleTask(it, context) },
                    onRemoveFromTop5 = { viewModel.removeFromTop5(it) }
                )
            }

            // Time Slot Routine Cards
            item {
                Text(
                    text = "Daily Routine Schedule",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Morning
            item {
                RoutineSectionCard(
                    slot = TimeSlot.MORNING,
                    tasks = uiState.morningTasks,
                    onToggleTask = { viewModel.toggleTask(it, context) },
                    onToggleTop5 = { viewModel.toggleTop5(it) },
                    onDeleteTask = { viewModel.deleteTask(it, context) },
                    onAddTaskInSlot = { slot ->
                        dialogInitialSlot = slot
                        dialogInitialIsTop5 = false
                        dialogTop5SlotIndex = -1
                        showAddTaskDialog = true
                    }
                )
            }

            // Afternoon
            item {
                RoutineSectionCard(
                    slot = TimeSlot.AFTERNOON,
                    tasks = uiState.afternoonTasks,
                    onToggleTask = { viewModel.toggleTask(it, context) },
                    onToggleTop5 = { viewModel.toggleTop5(it) },
                    onDeleteTask = { viewModel.deleteTask(it, context) },
                    onAddTaskInSlot = { slot ->
                        dialogInitialSlot = slot
                        dialogInitialIsTop5 = false
                        dialogTop5SlotIndex = -1
                        showAddTaskDialog = true
                    }
                )
            }

            // Evening
            item {
                RoutineSectionCard(
                    slot = TimeSlot.EVENING,
                    tasks = uiState.eveningTasks,
                    onToggleTask = { viewModel.toggleTask(it, context) },
                    onToggleTop5 = { viewModel.toggleTop5(it) },
                    onDeleteTask = { viewModel.deleteTask(it, context) },
                    onAddTaskInSlot = { slot ->
                        dialogInitialSlot = slot
                        dialogInitialIsTop5 = false
                        dialogTop5SlotIndex = -1
                        showAddTaskDialog = true
                    }
                )
            }

            // Night
            item {
                RoutineSectionCard(
                    slot = TimeSlot.NIGHT,
                    tasks = uiState.nightTasks,
                    onToggleTask = { viewModel.toggleTask(it, context) },
                    onToggleTop5 = { viewModel.toggleTop5(it) },
                    onDeleteTask = { viewModel.deleteTask(it, context) },
                    onAddTaskInSlot = { slot ->
                        dialogInitialSlot = slot
                        dialogInitialIsTop5 = false
                        dialogTop5SlotIndex = -1
                        showAddTaskDialog = true
                    }
                )
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                dialogInitialSlot = TimeSlot.MORNING
                dialogInitialIsTop5 = false
                dialogTop5SlotIndex = -1
                showAddTaskDialog = true
            },
            containerColor = DragonGreen,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_task_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.Black
                )
                Text(
                    text = "Add task",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            initialSlot = dialogInitialSlot,
            initialIsTop5 = dialogInitialIsTop5,
            initialTop5SlotIndex = dialogTop5SlotIndex,
            selectedDate = uiState.selectedDate,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, slot, isTop5, slotIdx, scheduledTime, scheduledTimeMillis ->
                viewModel.addTask(
                    title = title,
                    slot = slot,
                    isTop5 = isTop5,
                    top5Index = slotIdx,
                    scheduledTime = scheduledTime,
                    scheduledTimeMillis = scheduledTimeMillis,
                    context = context
                )
                showAddTaskDialog = false
            }
        )
    }

    // Pick Top 5 Dialog
    if (showPickTop5Dialog) {
        PickTop5TaskDialog(
            slotIndex = selectedTop5SlotIndex,
            availableTasks = uiState.availableTasksForTop5,
            onDismiss = { showPickTop5Dialog = false },
            onAssignExisting = { task, slotIdx ->
                viewModel.assignExistingToTop5(task, slotIdx)
            },
            onCreateNew = { slotIdx ->
                dialogInitialSlot = TimeSlot.MORNING
                dialogInitialIsTop5 = true
                dialogTop5SlotIndex = slotIdx
                showAddTaskDialog = true
            }
        )
    }
}
