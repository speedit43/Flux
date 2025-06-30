package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel
import com.flux.ui.events.HabitEvents
import com.flux.ui.screens.events.IconRadioButton
import com.flux.ui.screens.events.toFormattedDate
import com.flux.ui.screens.events.toFormattedTime
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun EmptyHabits(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EventAvailable,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text("No Habit Found")
    }
}

@Composable
fun HabitDateCard(radius:Int, isDone:Boolean, isTodayDone: Boolean, day: String, date: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = shapeManager(radius=radius*2),
        colors = CardDefaults.cardColors(
            containerColor = if (isTodayDone && isDone) MaterialTheme.colorScheme.primary else if (isTodayDone) MaterialTheme.colorScheme.primaryContainer else if (isDone) MaterialTheme.colorScheme.secondary else  MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = if (isTodayDone && isDone) MaterialTheme.colorScheme.onPrimary else if (isTodayDone) MaterialTheme.colorScheme.onPrimaryContainer else if(isDone) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            Modifier.fillMaxWidth().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(day.uppercase(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraLight), modifier= Modifier.alpha(0.95f))
            Text(date.toString(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraLight), modifier= Modifier.alpha(0.95f))
        }
    }
}

@Composable
fun HabitPreviewCard(
    radius: Int,
    habit: HabitModel,
    instances: List<HabitInstanceModel>,
    onToggleDone: (LocalDate) -> Unit,
    onAnalyticsClicked: () -> Unit
) {
    val today = LocalDate.now()
    val isTodayDone = instances.any { it.instanceDate == today }
    val monday = today.with(DayOfWeek.MONDAY)
    val weekDates = (0..6).map { monday.plusDays(it.toLong()) }
    val (currentStreak, _) = calculateStreaks(instances)

    Card(
        onClick = {onToggleDone(today)},
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isTodayDone) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer),
        shape = shapeManager(radius=radius*2)
    ) {
        Column {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTodayDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                shape = shapeManager(radius=radius*2)
            ) {
                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            IconRadioButton(
                                selected = isTodayDone,
                                checkedTint = MaterialTheme.colorScheme.onTertiary,
                                uncheckedTint = MaterialTheme.colorScheme.onTertiary
                            ) { onToggleDone(today) }
                            Column {
                                Text(habit.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    habit.startDateTime.toFormattedTime(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraLight),
                                    modifier = Modifier.alpha(0.9f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton({}) { Icon(Icons.Default.LocalFireDepartment, null) }
                            Text("$currentStreak")
                            IconButton(onAnalyticsClicked) { Icon(Icons.Default.Analytics, null) }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekDates.forEach { date ->
                    val isDone = instances.any { it.instanceDate == date }
                    HabitDateCard(radius, isDone, isTodayDone, date.dayOfWeek.name.take(3), date.dayOfMonth, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HabitCalenderCard(radius: Int, habitId: Int, workspaceId: Int, startDateTime: Long, habitInstances: List<HabitInstanceModel>, onHabitEvents: (HabitEvents)->Unit){
    val habitStartMonth = Instant.ofEpochMilli(startDateTime).atZone(ZoneId.systemDefault()).toLocalDate().let { YearMonth.of(it.year, it.month) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val currentYearMonth = YearMonth.now()
    val endOfYear = YearMonth.of(currentYearMonth.year, 12)
    val canGoBack = currentMonth > habitStartMonth
    val canGoForward = currentMonth < endOfYear
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
    val dates = (1..daysInMonth).map { currentMonth.atDay(it) }
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Card(
        modifier = Modifier.fillMaxWidth().height(330.dp),
        onClick = {},
        shape = shapeManager(radius = radius*2),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { if (canGoBack) currentMonth = currentMonth.minusMonths(1) },
                    enabled = canGoBack
                ) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBackIos,
                        contentDescription = "Previous Month",
                        modifier = Modifier
                            .alpha(if (canGoBack) 0.8f else 0.3f)
                            .size(16.dp)
                    )
                }
                Text(text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " ${currentMonth.year}", style = MaterialTheme.typography.titleMedium)
                IconButton(
                    onClick = { if (canGoForward) currentMonth = currentMonth.plusMonths(1) },
                    enabled = canGoForward
                ) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowForwardIos,
                        contentDescription = "Next Month",
                        modifier = Modifier
                            .alpha(if (canGoForward) 0.8f else 0.3f)
                            .size(16.dp)
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                daysOfWeek.forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Add empty boxes for alignment before 1st day
                items(firstDayOfWeek) { Box(modifier = Modifier.size(32.dp)) }

                items(dates) { date ->
                    val instance = habitInstances.find { it.instanceDate == date }
                    val isMarked = instance != null
                    val backgroundColor = if (isMarked) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else Color.Transparent
                    val textColor = if (isMarked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    val habitStartDate = Instant.ofEpochMilli(startDateTime).atZone(ZoneId.systemDefault()).toLocalDate()
                    val isBeforeStart = date.isBefore(habitStartDate)
                    val dateAlpha = if (isBeforeStart) 0.2f else 1f

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .alpha(dateAlpha)
                            .clickable(enabled = !isBeforeStart) {
                                if (isMarked) { onHabitEvents(HabitEvents.MarkUndone(instance)) }
                                else { onHabitEvents(HabitEvents.MarkDone(HabitInstanceModel(habitId = habitId, instanceDate = date, workspaceId = workspaceId))) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(date.dayOfMonth.toString(), color = textColor)
                    }
                }
            }
        }
    }
}

@Composable
fun HabitStreakCard(currentStreak: Int, bestStreak: Int, radius: Int){
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {},
        shape = shapeManager(radius = radius*2),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Current Streak: $currentStreak days", modifier = Modifier.alpha(0.85f))
                Text("Best: $bestStreak days", fontWeight = FontWeight.SemiBold)
            }
            CircleWrapper(MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.LocalFireDepartment, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun HabitStartCard(startDateTime: Long, radius: Int){
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {},
        shape = shapeManager(radius=radius*2),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
    ) {
        Row(Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Started on", modifier = Modifier.alpha(0.85f))
                Text(startDateTime.toFormattedDate(), fontWeight = FontWeight.SemiBold)
            }
            CircleWrapper(MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Flag, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

fun calculateStreaks(instances: List<HabitInstanceModel>): Pair<Int, Int> {
    if (instances.isEmpty()) return 0 to 0

    val today = LocalDate.now()
    val dates = instances.map { it.instanceDate }.sorted()

    var bestStreak = 0
    var currentStreak = 0
    var streak = 0
    var previousDate: LocalDate? = null

    for (date in dates) {
        if (previousDate == null || date == previousDate.plusDays(1)) {
            streak++
        } else if (date != previousDate) {
            streak = 1
        }
        bestStreak = maxOf(bestStreak, streak)
        previousDate = date
    }

    // Calculate current streak (ending at today or yesterday)
    var cursor = today
    currentStreak = 0
    while (dates.contains(cursor)) {
        currentStreak++
        cursor = cursor.minusDays(1)
    }

    return currentStreak to bestStreak
}
