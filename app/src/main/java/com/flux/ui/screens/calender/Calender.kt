package com.flux.ui.screens.calender

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.R
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.data.model.EventStatus
import com.flux.navigation.Loader
import com.flux.navigation.NavRoutes
import com.flux.ui.components.DailyViewCalender
import com.flux.ui.components.MonthlyViewCalender
import com.flux.ui.events.SettingEvents
import com.flux.ui.events.TaskEvents
import com.flux.ui.screens.events.EmptyEvents
import com.flux.ui.screens.events.EventCard
import com.flux.ui.state.Settings
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calender(
    navController: NavController,
    radius: Int,
    isLoading: Boolean,
    workspaceId: Long,
    settings: Settings,
    datedEvents: List<EventModel>,
    allEventInstances: List<EventInstanceModel>,
    onSettingEvents: (SettingEvents) ->Unit,
    onTaskEvents: (TaskEvents)->Unit
) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val isMonthlyView=settings.data.isCalenderMonthlyView

    val pendingTasks = datedEvents.filter { task ->
        val instance = allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }
        instance == null || instance.status== EventStatus.PENDING
    }

    val completedTasks = datedEvents.filter { task ->
        val instance = allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }
        instance != null && instance.status== EventStatus.COMPLETED
    }

    LaunchedEffect(Unit) { onTaskEvents(TaskEvents.LoadDateTask(workspaceId, selectedDate)) }

    LazyColumn(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if(isMonthlyView){
            item {
                MonthlyViewCalender(selectedMonth, selectedDate, onMonthChange = { selectedMonth=it },
                    onDateChange = {
                        onTaskEvents(TaskEvents.LoadDateTask(workspaceId, it))
                        selectedDate=it
                    }) }
            }
        else {
            item{
                DailyViewCalender(selectedMonth, selectedDate, onMonthChange = { selectedMonth=it }, onDateChange = {
                    onTaskEvents(TaskEvents.LoadDateTask(workspaceId, it))
                    selectedDate=it
                }) }
            }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.Monthly_View), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = isMonthlyView,
                        onCheckedChange = { onSettingEvents(SettingEvents.UpdateSettings(settings.data.copy(isCalenderMonthlyView = it))) },
                        thumbContent = { if(isMonthlyView){ Icon(Icons.Default.Check, null) } }
                    )
                }
            }
        }
        item { HorizontalDivider() }
        if(isLoading) { item { Loader() } }
        else if(datedEvents.isEmpty()){ item { EmptyEvents() } }
        else {
            if (pendingTasks.isNotEmpty()) {
                items(pendingTasks) { task ->
                    val instance =
                        allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }
                            ?: EventInstanceModel(eventId = task.eventId, instanceDate = selectedDate, workspaceId = workspaceId)

                    EventCard(
                        radius=radius,
                        isAllDay = task.isAllDay,
                        eventInstance = instance,
                        title = task.title,
                        timeline = task.startDateTime,
                        description = task.description,
                        repeat = task.repetition,
                        onChangeStatus = { onTaskEvents(TaskEvents.ToggleStatus(it)) },
                        onClick = {
                            navController.navigate(
                                NavRoutes.EventDetails.withArgs(
                                    workspaceId,
                                    task.eventId
                                )
                            )
                        }
                    )
                }
            }
            if (completedTasks.isNotEmpty()) {
                items(completedTasks) { task ->
                    val instance = allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }!!

                    EventCard(
                        radius=radius,
                        isAllDay = task.isAllDay,
                        eventInstance = instance,
                        title = task.title,
                        timeline = task.startDateTime,
                        description = task.description,
                        repeat = task.repetition,
                        onChangeStatus = { onTaskEvents(TaskEvents.ToggleStatus(it)) },
                        onClick = { navController.navigate(NavRoutes.EventDetails.withArgs(workspaceId, task.eventId)) }
                    )
                }
            }
        }
    }
}