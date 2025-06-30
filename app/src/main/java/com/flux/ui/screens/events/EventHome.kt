package com.flux.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.data.model.EventStatus
import com.flux.data.model.Repetition
import com.flux.navigation.Loader
import com.flux.navigation.NavRoutes
import com.flux.ui.components.shapeManager
import com.flux.ui.events.TaskEvents
import com.flux.ui.theme.completed
import com.flux.ui.theme.pending
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventHome(
    navController: NavController,
    radius: Int,
    isLoading: Boolean,
    allEvents: List<EventModel>,
    allEventInstances: List<EventInstanceModel>,
    workspaceId: Int,
    onTaskEvents: (TaskEvents)->Unit
) {
    if(isLoading) { Loader() }
    else if(allEvents.isEmpty()){ EmptyEvents() }
    else {
        val today = LocalDate.now()

        val pendingTasks = allEvents.filter { event ->
            val instance = allEventInstances.find { it.eventId == event.eventId && it.instanceDate == today }
            instance == null || instance.status== EventStatus.PENDING
        }

        val completedTasks = allEvents.filter { event ->
            val instance = allEventInstances.find { it.eventId == event.eventId && it.instanceDate == today }
            instance != null && instance.status== EventStatus.COMPLETED
        }

        Column(
            modifier = Modifier.padding(top = 24.dp, end = 8.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (pendingTasks.isNotEmpty()) {
                pendingTasks.forEach { event->
                    val instance = allEventInstances.find { it.eventId == event.eventId && it.instanceDate == today }
                        ?: EventInstanceModel(eventId = event.eventId, instanceDate = today, workspaceId = workspaceId)

                    EventCard(
                        radius=radius,
                        isAllDay = event.isAllDay,
                        eventInstance = instance,
                        title = event.title,
                        timeline = event.startDateTime,
                        description = event.description,
                        repeat = event.repetition,
                        onChangeStatus = { onTaskEvents(TaskEvents.ToggleStatus(it)) },
                        onClick = { navController.navigate(NavRoutes.EventDetails.withArgs(workspaceId, event.eventId)) }
                    )
                }
            }

            if (completedTasks.isNotEmpty()) {
                completedTasks.forEach{ event ->
                    val instance = allEventInstances.find { it.eventId == event.eventId && it.instanceDate == today }!!

                    EventCard(
                        radius=radius,
                        isAllDay = event.isAllDay,
                        eventInstance = instance,
                        title = event.title,
                        timeline = event.startDateTime,
                        description = event.description,
                        repeat = event.repetition,
                        onChangeStatus = { onTaskEvents(TaskEvents.ToggleStatus(it)) },
                        onClick = {
                            navController.navigate(NavRoutes.EventDetails.withArgs(workspaceId, event.eventId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyEvents(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.TaskAlt,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text("No Event Found")
    }
}

@Composable
fun IconRadioButton(
    modifier: Modifier= Modifier,
    uncheckedTint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    checkedTint: Color = completed,
    selected: Boolean,
    onClick: () -> Unit
) {
    IconButton(modifier=modifier, onClick = onClick) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = checkedTint
            )
        } else {
            Icon(
                imageVector = Icons.Default.RadioButtonUnchecked,
                contentDescription = "Unselected",
                tint = uncheckedTint
            )
        }
    }
}

@Composable
fun EventCard(
    radius: Int,
    isAllDay: Boolean,
    eventInstance: EventInstanceModel,
    title: String,
    timeline: Long,
    description: String,
    repeat: Repetition,
    onChangeStatus: (EventInstanceModel)->Unit,
    onClick: () -> Unit
) {
    val eventStatus=eventInstance.status
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconRadioButton(
            selected = eventStatus == EventStatus.COMPLETED,
            onClick = {
                val newStatus = if (eventStatus == EventStatus.COMPLETED) EventStatus.PENDING else EventStatus.COMPLETED
                onChangeStatus(eventInstance.copy(status = newStatus))
            }
        )
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().height(if (description.isEmpty()) 55.dp else 76.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = shapeManager(radius=radius*2),
            onClick = onClick
        ) {
            Row(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(if (description.isEmpty()) 55.dp else 76.dp)
                        .background(
                            if(eventStatus== EventStatus.PENDING) pending else completed,
                            RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                        )
                )

                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        if(repeat!= Repetition.NONE){
                                Row(
                                    modifier = Modifier.padding(end = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Icon(
                                        Icons.Default.Repeat,
                                        null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        repeat.toString(),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.alpha(0.9f)
                                    )
                                }

                        }
                        Text(
                            when {
                                isAllDay -> "All Day"
                                repeat == Repetition.DAILY -> timeline.toFormattedDailyTime()
                                repeat == Repetition.WEEKLY -> timeline.toFormattedWeeklyTime()
                                repeat == Repetition.MONTHLY -> timeline.toFormattedMonthlyTime()
                                repeat == Repetition.YEARLY -> timeline.toFormattedYearlyTime()
                                else -> timeline.toFormattedDateTime()
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(0.6f)
                        )
                    }
                    Text(
                        description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

fun Long.toFormattedTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(date)
}

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
    return format.format(date)
}


fun Long.toFormattedDateTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM, yyyy  'at' hh:mm a", Locale.getDefault())
    return format.format(date)
}

fun Long.toFormattedWeeklyTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("EEEE  'at' hh:mm a", Locale.getDefault()) // Only day name
    return format.format(date)
}

fun Long.toFormattedMonthlyTime(): String {
    return this.toOrdinalFormatted("")
}

fun Long.toFormattedYearlyTime(): String {
    return this.toOrdinalFormatted("MMM ")
}

fun Long.toFormattedDailyTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("'at' hh:mm a", Locale.getDefault())
    return format.format(date)
}

private fun Long.toOrdinalFormatted(pattern: String): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@toOrdinalFormatted }
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val suffix = getDayOfMonthSuffix(day)
    val datePart = SimpleDateFormat("d'$suffix' $pattern", Locale.getDefault()).format(Date(this))
    val timePart = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))
    return "on $datePart at $timePart"
}

private fun getDayOfMonthSuffix(day: Int): String {
    return if (day in 11..13) {
        "th"
    } else {
        when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}
