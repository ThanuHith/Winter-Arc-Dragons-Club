package com.example.ui.screens

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassmorphicCard
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

private data class MoodOption(val key: String, val label: String, val emoji: String, val activeColor: Color)

private val MOOD_OPTIONS = listOf(
    MoodOption("EXCELLENT", "Excellent", "🔥", Color(0xFFFF5722)),
    MoodOption("GOOD", "Good", "💪", DragonGreen),
    MoodOption("AVERAGE", "Average", "😐", NeonYellow),
    MoodOption("DIFFICULT", "Difficult", "😩", ElectricPink)
)

@Composable
fun ReviewScreen(
    viewModel: WinterArcViewModel,
    modifier: Modifier = Modifier
) {
    val reflection by viewModel.dailyReflection.collectAsStateWithLifecycle()
    val weeklyReview by viewModel.weeklyReview.collectAsStateWithLifecycle()
    val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("review_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                            .background(ElectricPink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = ElectricPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Review & Reflection",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reflect daily. Synthesize weekly. Calibrate your trajectory.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // Daily Mood Selector Card
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                hasGradientGlow = true,
                backdropRes = com.example.R.drawable.img_graffiti_reflection_1790913737414
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Feeling Today",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.resetDailyMood() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Mood",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MOOD_OPTIONS.forEach { opt ->
                            val isSelected = reflection.mood == opt.key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) opt.activeColor.copy(alpha = 0.25f)
                                        else Color(0x10FFFFFF)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) opt.activeColor else Color(0x22FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.updateReflection(mood = opt.key) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = opt.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = opt.label,
                                        color = if (isSelected) opt.activeColor else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Reflection Section
        item {
            Text(
                text = "Daily Reflection",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CyberCardBgTranslucent,
                backdropRes = com.example.R.drawable.img_graffiti_reflection_1790913737414
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ReflectionInputField(
                        label = "What went well today?",
                        value = reflection.wentWell,
                        placeholder = "Wins, completed deep work, adherence to schedule...",
                        onValueChange = { viewModel.updateReflection(wentWell = it) },
                        testTag = "went_well_input"
                    )

                    ReflectionInputField(
                        label = "What distracted me?",
                        value = reflection.distracted,
                        placeholder = "Mindless scrolling, interruptions, energy dips...",
                        onValueChange = { viewModel.updateReflection(distracted = it) },
                        testTag = "distracted_input"
                    )

                    ReflectionInputField(
                        label = "What will I improve tomorrow?",
                        value = reflection.improveTomorrow,
                        placeholder = "Actionable adjustment for tomorrow's execution...",
                        onValueChange = { viewModel.updateReflection(improveTomorrow = it) },
                        testTag = "improve_tomorrow_input"
                    )

                    ReflectionInputField(
                        label = "⭐ One thing I'm proud of today",
                        value = reflection.proudOf,
                        placeholder = "A hard task pushed through, a moment of discipline...",
                        accentColor = NeonYellow,
                        onValueChange = { viewModel.updateReflection(proudOf = it) },
                        testTag = "proud_of_input"
                    )
                }
            }
        }

        // Weekly Review Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Review",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = weeklyStats.weekRangeDisplay,
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1F00E676))
                        .border(1.dp, DragonGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${weeklyStats.completedTasks}/${weeklyStats.totalTasks} tasks",
                        color = DragonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CyberCardBgTranslucent,
                backdropRes = com.example.R.drawable.img_graffiti_arena_1790913721295
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ReflectionInputField(
                        label = "Biggest win",
                        value = weeklyReview.biggestWin,
                        placeholder = "Your standout victory or milestone this week...",
                        onValueChange = { viewModel.updateWeeklyReview(biggestWin = it) },
                        testTag = "biggest_win_input"
                    )

                    ReflectionInputField(
                        label = "Biggest problem",
                        value = weeklyReview.biggestProblem,
                        placeholder = "The main bottleneck, mistake, or breakdown...",
                        onValueChange = { viewModel.updateWeeklyReview(biggestProblem = it) },
                        testTag = "biggest_problem_input"
                    )

                    ReflectionInputField(
                        label = "What worked for me?",
                        value = weeklyReview.whatWorked,
                        placeholder = "Routines, environments, mental models that yielded results...",
                        onValueChange = { viewModel.updateWeeklyReview(whatWorked = it) },
                        testTag = "what_worked_input"
                    )

                    ReflectionInputField(
                        label = "What should I stop or change?",
                        value = weeklyReview.whatToChange,
                        placeholder = "Habits, behaviors, or time-wasters to eliminate...",
                        onValueChange = { viewModel.updateWeeklyReview(whatToChange = it) },
                        testTag = "what_to_change_input"
                    )

                    ReflectionInputField(
                        label = "🎯 Next week's Top 5",
                        value = weeklyReview.nextWeekTop5,
                        placeholder = "1. \n2. \n3. \n4. \n5. ",
                        accentColor = ElectricPink,
                        minLines = 4,
                        onValueChange = { viewModel.updateWeeklyReview(nextWeekTop5 = it) },
                        testTag = "next_week_top5_input"
                    )

                    ReflectionInputField(
                        label = "What did I learn about myself?",
                        value = weeklyReview.learnedAboutSelf,
                        placeholder = "Self-awareness insights, triggers, peak energy hours...",
                        onValueChange = { viewModel.updateWeeklyReview(learnedAboutSelf = it) },
                        testTag = "learned_about_self_input"
                    )
                }
            }
        }
    }
}

@Composable
private fun ReflectionInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    testTag: String,
    accentColor: Color = CyberCyan,
    minLines: Int = 2
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = if (accentColor != CyberCyan) accentColor else TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted.copy(alpha = 0.5f), fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines,
            maxLines = 6,
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
