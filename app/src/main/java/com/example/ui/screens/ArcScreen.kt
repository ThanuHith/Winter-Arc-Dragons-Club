package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.NeonProgressBar
import com.example.ui.theme.CyberBorderStroke
import com.example.ui.theme.CyberCardBgTranslucent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberInputBg
import com.example.ui.theme.DragonGreen
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.WinterArcViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private data class FocusTag(val label: String, val emoji: String)

private val FOCUS_TAGS = listOf(
    FocusTag("Fitness", "🏋️"),
    FocusTag("Learning", "📚"),
    FocusTag("Career", "💼"),
    FocusTag("Finance", "💰"),
    FocusTag("Content", "🎥"),
    FocusTag("Personal", "🧠"),
    FocusTag("Recovery", "🩹"),
    FocusTag("Other", "✨")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArcScreen(
    viewModel: WinterArcViewModel,
    modifier: Modifier = Modifier
) {
    val strategy by viewModel.arcStrategy.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Expanded states for Day 1, 30, 60, 90
    val expandedMilestones = remember {
        mutableStateMapOf(
            1 to true,
            30 to false,
            60 to false,
            90 to false
        )
    }

    // Parse dates and calculate progress
    val today = LocalDate.now()
    var startLocalDate: LocalDate? = null
    var endLocalDate: LocalDate? = null
    try {
        if (strategy.startDate.isNotBlank()) startLocalDate = LocalDate.parse(strategy.startDate)
        if (strategy.endDate.isNotBlank()) endLocalDate = LocalDate.parse(strategy.endDate)
    } catch (_: Exception) {}

    val arcTotalDays = if (startLocalDate != null && endLocalDate != null && !endLocalDate.isBefore(startLocalDate)) {
        ChronoUnit.DAYS.between(startLocalDate, endLocalDate).coerceAtLeast(1)
    } else 90L

    val daysElapsed = if (startLocalDate != null) {
        if (today.isBefore(startLocalDate)) 0L
        else ChronoUnit.DAYS.between(startLocalDate, today).coerceIn(0L, arcTotalDays)
    } else 0L

    val arcProgress = if (arcTotalDays > 0) (daysElapsed.toFloat() / arcTotalDays.toFloat()).coerceIn(0f, 1f) else 0f
    val isNotStarted = startLocalDate != null && today.isBefore(startLocalDate)
    val progressLabel = when {
        isNotStarted -> "Not started"
        startLocalDate == null -> "Not configured"
        daysElapsed >= arcTotalDays -> "Completed!"
        else -> "Day $daysElapsed of $arcTotalDays"
    }

    val selectedTagsSet = remember(strategy.focusTagsCsv) {
        if (strategy.focusTagsCsv.isBlank()) emptySet()
        else strategy.focusTagsCsv.split(",").map { it.trim() }.toSet()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("arc_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "90-Day Arc Strategy",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lock in your dates, focus pillars, and 30-day milestone retrospectives.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // Arc Progress Card
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                hasGradientGlow = true,
                backdropRes = com.example.R.drawable.img_graffiti_arena_1790913721295
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
                            val percentInt = (arcProgress * 100).toInt()
                            Text(
                                text = "$percentInt%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = DragonGreen
                            )
                            Text(
                                text = progressLabel,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x1F00F0FF))
                                .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$arcTotalDays Days Arc",
                                color = CyberCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    NeonProgressBar(
                        progress = arcProgress,
                        height = 10.dp
                    )
                }
            }
        }

        // Date Configuration & Main Goal
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CyberCardBgTranslucent
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Arc Timeline",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Start Date Picker
                        DateButton(
                            label = "Start Date",
                            dateValue = strategy.startDate.ifBlank { "Select Start" },
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val cur = startLocalDate ?: LocalDate.now()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val newDate = LocalDate.of(year, month + 1, day)
                                        viewModel.updateArcStrategy(startDate = newDate.toString())
                                    },
                                    cur.year, cur.monthValue - 1, cur.dayOfMonth
                                ).show()
                            }
                        )

                        // End Date Picker
                        DateButton(
                            label = "End Date",
                            dateValue = strategy.endDate.ifBlank { "Select End" },
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val cur = endLocalDate ?: LocalDate.now().plusDays(90)
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val newDate = LocalDate.of(year, month + 1, day)
                                        viewModel.updateArcStrategy(endDate = newDate.toString())
                                    },
                                    cur.year, cur.monthValue - 1, cur.dayOfMonth
                                ).show()
                            }
                        )
                    }

                    // Main Goal
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Main Arc Goal",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedTextField(
                            value = strategy.mainGoal,
                            onValueChange = { viewModel.updateArcStrategy(mainGoal = it) },
                            placeholder = { Text("What single monumental transformation defines this Arc?", color = TextMuted.copy(alpha = 0.5f), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("main_goal_input"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = CyberInputBg,
                                unfocusedContainerColor = CyberInputBg,
                                focusedBorderColor = DragonGreen,
                                unfocusedBorderColor = CyberBorderStroke,
                                cursorColor = DragonGreen
                            )
                        )
                    }

                    // Focus Tags
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Focus Pillars",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FOCUS_TAGS.forEach { tag ->
                                val isSelected = selectedTagsSet.contains(tag.label)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) ElectricPink.copy(alpha = 0.25f)
                                            else Color(0x10FFFFFF)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) ElectricPink else Color(0x22FFFFFF),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            val newSet = if (isSelected) selectedTagsSet - tag.label
                                            else selectedTagsSet + tag.label
                                            viewModel.updateArcStrategy(focusTagsCsv = newSet.joinToString(","))
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${tag.emoji} ${tag.label}",
                                        color = if (isSelected) ElectricPink else TextMuted,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Milestone Blocks Header
        item {
            Text(
                text = "Arc Milestones",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Day 1
        item {
            MilestoneCard(
                dayNumber = 1,
                title = "Day 1 Baseline",
                isExpanded = expandedMilestones[1] ?: true,
                onToggleExpand = { expandedMilestones[1] = !(expandedMilestones[1] ?: true) },
                improveValue = strategy.milestoneDay1Improve,
                happenedValue = strategy.milestoneDay1Happened,
                lessonsValue = strategy.milestoneDay1Lessons,
                onImproveChange = { viewModel.updateArcStrategy(day1Improve = it) },
                onHappenedChange = { viewModel.updateArcStrategy(day1Happened = it) },
                onLessonsChange = { viewModel.updateArcStrategy(day1Lessons = it) }
            )
        }

        // Day 30
        item {
            MilestoneCard(
                dayNumber = 30,
                title = "Day 30 Checkpoint",
                isExpanded = expandedMilestones[30] ?: false,
                onToggleExpand = { expandedMilestones[30] = !(expandedMilestones[30] ?: false) },
                improveValue = strategy.milestoneDay30Improve,
                happenedValue = strategy.milestoneDay30Happened,
                lessonsValue = strategy.milestoneDay30Lessons,
                onImproveChange = { viewModel.updateArcStrategy(day30Improve = it) },
                onHappenedChange = { viewModel.updateArcStrategy(day30Happened = it) },
                onLessonsChange = { viewModel.updateArcStrategy(day30Lessons = it) }
            )
        }

        // Day 60
        item {
            MilestoneCard(
                dayNumber = 60,
                title = "Day 60 Momentum",
                isExpanded = expandedMilestones[60] ?: false,
                onToggleExpand = { expandedMilestones[60] = !(expandedMilestones[60] ?: false) },
                improveValue = strategy.milestoneDay60Improve,
                happenedValue = strategy.milestoneDay60Happened,
                lessonsValue = strategy.milestoneDay60Lessons,
                onImproveChange = { viewModel.updateArcStrategy(day60Improve = it) },
                onHappenedChange = { viewModel.updateArcStrategy(day60Happened = it) },
                onLessonsChange = { viewModel.updateArcStrategy(day60Lessons = it) }
            )
        }

        // Day 90
        item {
            MilestoneCard(
                dayNumber = 90,
                title = "Day 90 Culmination",
                isExpanded = expandedMilestones[90] ?: false,
                onToggleExpand = { expandedMilestones[90] = !(expandedMilestones[90] ?: false) },
                improveValue = strategy.milestoneDay90Improve,
                happenedValue = strategy.milestoneDay90Happened,
                lessonsValue = strategy.milestoneDay90Lessons,
                onImproveChange = { viewModel.updateArcStrategy(day90Improve = it) },
                onHappenedChange = { viewModel.updateArcStrategy(day90Happened = it) },
                onLessonsChange = { viewModel.updateArcStrategy(day90Lessons = it) }
            )
        }
    }
}

@Composable
private fun DateButton(
    label: String,
    dateValue: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberInputBg)
                .border(1.dp, CyberBorderStroke, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateValue,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun MilestoneCard(
    dayNumber: Int,
    title: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    improveValue: String,
    happenedValue: String,
    lessonsValue: String,
    onImproveChange: (String) -> Unit,
    onHappenedChange: (String) -> Unit,
    onLessonsChange: (String) -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CyberCardBgTranslucent,
        backdropRes = com.example.R.drawable.img_graffiti_arena_1790913721295
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when (dayNumber) {
                                    1 -> DragonGreen.copy(alpha = 0.25f)
                                    30 -> CyberCyan.copy(alpha = 0.25f)
                                    60 -> NeonYellow.copy(alpha = 0.25f)
                                    else -> ElectricPink.copy(alpha = 0.25f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "D$dayNumber",
                            color = when (dayNumber) {
                                1 -> DragonGreen
                                30 -> CyberCyan
                                60 -> NeonYellow
                                else -> ElectricPink
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Text(
                        text = title,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = TextMuted
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MilestoneField(
                        label = "What I wanted to improve",
                        value = improveValue,
                        placeholder = "Intentions, targets, habits intended for this phase...",
                        onValueChange = onImproveChange,
                        testTag = "milestone_${dayNumber}_improve"
                    )

                    MilestoneField(
                        label = "What actually happened",
                        value = happenedValue,
                        placeholder = "Reality check, facts, setbacks and successes...",
                        onValueChange = onHappenedChange,
                        testTag = "milestone_${dayNumber}_happened"
                    )

                    MilestoneField(
                        label = "Lessons",
                        value = lessonsValue,
                        placeholder = "Key takeaways to adjust subsequent phases...",
                        accentColor = NeonYellow,
                        onValueChange = onLessonsChange,
                        testTag = "milestone_${dayNumber}_lessons"
                    )
                }
            }
        }
    }
}

@Composable
private fun MilestoneField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    testTag: String,
    accentColor: Color = CyberCyan
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = if (accentColor != CyberCyan) accentColor else TextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted.copy(alpha = 0.5f), fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(10.dp),
            minLines = 2,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = CyberInputBg,
                unfocusedContainerColor = CyberInputBg,
                focusedBorderColor = accentColor,
                unfocusedBorderColor = CyberBorderStroke,
                cursorColor = accentColor
            )
        )
    }
}
