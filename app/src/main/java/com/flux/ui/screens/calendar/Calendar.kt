package com.flux.ui.screens.calender

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import com.flux.data.model.EventStatus
import com.flux.navigation.Loader
import com.flux.navigation.NavRoutes
import com.flux.ui.components.DailyViewCalender
import com.flux.ui.components.MonthlyViewCalender
import com.flux.ui.events.TaskEvents
import com.flux.ui.screens.events.EmptyEvents
import com.flux.ui.screens.events.EventCard
import com.flux.ui.state.Settings
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.calenderItems(
    navController: NavController,
    radius: Int,
    isLoading: Boolean,
    workspaceId: Long,
    selectedMonth: YearMonth,
    selectedDate: LocalDate,
    settings: Settings,
    datedEvents: List<EventModel>,
    allEventInstances: List<EventInstanceModel>,
    onTaskEvents: (TaskEvents) -> Unit
) {
    val isMonthlyView = settings.data.isCalenderMonthlyView

    if (isMonthlyView) {
        item {
            MonthlyViewCalender(
                selectedMonth, selectedDate,
                onMonthChange = {
                    onTaskEvents(TaskEvents.ChangeMonth(it))
                },
                onDateChange = {
                    onTaskEvents(TaskEvents.LoadDateTask(workspaceId, it))
                    onTaskEvents(TaskEvents.ChangeDate(it))
                })
        }
    } else {
        item {
            DailyViewCalender(
                selectedMonth,
                selectedDate,
                onDateChange = {
                    onTaskEvents(TaskEvents.LoadDateTask(workspaceId, it))
                    onTaskEvents(TaskEvents.ChangeDate(it))
                })
        }
    }
    if (isLoading) {
        item { Loader() }
    } else if (datedEvents.isEmpty()) {
        item { EmptyEvents() }
    } else {
        item { Spacer(Modifier.height(24.dp)) }

        val pendingTasks = datedEvents.filter { task ->
            val instance =
                allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }
            instance == null || instance.status == EventStatus.PENDING
        }

        val completedTasks = datedEvents.filter { task ->
            val instance =
                allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }
            instance != null && instance.status == EventStatus.COMPLETED
        }

        if (pendingTasks.isNotEmpty()) {
            items(pendingTasks) { task ->
                val instance =
                    allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }
                        ?: EventInstanceModel(
                            eventId = task.eventId,
                            instanceDate = selectedDate,
                            workspaceId = workspaceId
                        )

                EventCard(
                    radius = radius,
                    isAllDay = task.isAllDay,
                    eventInstance = instance,
                    title = task.title,
                    timeline = task.startDateTime,
                    description = task.description,
                    repeat = task.repetition,
                    settings = settings,
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
                val instance =
                    allEventInstances.find { it.eventId == task.eventId && it.instanceDate == selectedDate }!!

                EventCard(
                    radius = radius,
                    isAllDay = task.isAllDay,
                    eventInstance = instance,
                    title = task.title,
                    timeline = task.startDateTime,
                    description = task.description,
                    repeat = task.repetition,
                    settings = settings,
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
    }
}