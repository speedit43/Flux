package com.flux.ui.state

import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel

data class EventState(
    val isTodayEventLoading: Boolean = true,
    val isDatedEventLoading: Boolean = true,
    val allEvent: List<EventModel> = emptyList(),
    val todayEvents: List<EventModel> = emptyList(),
    val datedEvents: List<EventModel> = emptyList(),
    val allEventInstances: List<EventInstanceModel> = emptyList()
)
