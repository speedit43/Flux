package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun DailyViewDateCard(date: LocalDate, day: String, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.width(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = 0.6f),
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                day,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraLight),
                modifier = Modifier.padding(top = 4.dp)
            )
            ElevatedCard(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    date.dayOfMonth.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun MonthlyViewDateCard(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(containerColor)) {
        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor
        )
    }
}

@Composable
fun DailyViewCalendar(
    selectedMonth: YearMonth,
    selectedDate: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    val daysInMonth = selectedMonth.lengthOfMonth()
    val dateList = (1..daysInMonth).map { day -> selectedMonth.atDay(day) }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedMonth) {
        val todayIndex = dateList.indexOfFirst { it == selectedDate }
        if (todayIndex >= 0) {
            listState.animateScrollToItem(
                index = maxOf(0, todayIndex - 2)
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val month = selectedMonth
                onMonthChange(selectedMonth.minusMonths(1))
                onDateChange(month.minusMonths(1).atDay(1))
            }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Previous month")
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "${
                        selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }
                    }, ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = {
                val month = selectedMonth
                onMonthChange(selectedMonth.plusMonths(1))
                onDateChange(month.plusMonths(1).atDay(1))
            }) {
                Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            state = listState
        ) {
            items(dateList) { date ->
                val dayName =
                    date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercaseChar() }
                DailyViewDateCard(
                    date = date,
                    day = dayName,
                    isSelected = date == selectedDate,
                    onClick = { onDateChange(date) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyViewCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOffset = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    val allDates = buildList<LocalDate?> {
        repeat(firstDayOffset) { add(null) }
        for (day in 1..daysInMonth) {
            add(currentMonth.atDay(day))
        }
    }

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val month = currentMonth
                onMonthChange(currentMonth.minusMonths(1))
                onDateChange(month.minusMonths(1).atDay(1))
            }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBackIos,
                    contentDescription = "Previous month",
                    modifier = Modifier.size(18.dp)
                )
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = currentMonth.month.name.lowercase()
                        .replaceFirstChar { it.uppercaseChar() } + ", ${'$'}{currentMonth.year}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = {
                val month = currentMonth
                onMonthChange(currentMonth.plusMonths(1))
                onDateChange(month.plusMonths(1).atDay(1))
            }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowForwardIos,
                    contentDescription = "Next month",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Weekday Row
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

        Spacer(Modifier.height(4.dp))

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 300.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            userScrollEnabled = false
        ) {
            items(allDates) { date ->
                if (date == null) {
                    Box(modifier = Modifier.size(42.dp)) // blank space
                } else {
                    MonthlyViewDateCard(
                        date = date,
                        isSelected = selectedDate == date,
                        onClick = { onDateChange(date) }
                    )
                }
            }
        }
    }
}
