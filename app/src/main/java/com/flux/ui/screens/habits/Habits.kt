package com.flux.ui.screens.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.HabitInstanceModel
import com.flux.data.model.HabitModel
import com.flux.navigation.NavRoutes
import com.flux.ui.components.EmptyHabits
import com.flux.ui.components.HabitPreviewCard
import com.flux.ui.events.HabitEvents

@Composable
fun HabitsHome(
    navController: NavController,
    radius: Int,
    workspaceId: Long,
    allHabits: List<HabitModel>,
    allInstances: List<HabitInstanceModel>,
    onHabitEvents: (HabitEvents) -> Unit
) {
    if(allHabits.isEmpty()){ EmptyHabits() }
    else{
        LazyColumn(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allHabits) { habit ->
                val habitInstances = allInstances.filter { it.habitId == habit.habitId }
                HabitPreviewCard(
                    radius = radius,
                    habit = habit,
                    instances = habitInstances,
                    onToggleDone = { date ->
                        val existing = habitInstances.find { it.instanceDate == date }
                        if (existing != null) { onHabitEvents(HabitEvents.MarkUndone(existing)) }
                        else { onHabitEvents(HabitEvents.MarkDone(HabitInstanceModel(instanceDate = date, habitId = habit.habitId, workspaceId = workspaceId))) }
                    },
                    onAnalyticsClicked = { navController.navigate(NavRoutes.HabitDetails.withArgs(workspaceId, habit.habitId)) }
                )
            }
        }
    }
}