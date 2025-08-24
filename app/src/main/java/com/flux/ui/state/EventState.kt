package com.flux.ui.state

import com.flux.data.model.EventInstanceModel
import com.flux.data.model.EventModel
import java.time.LocalDate
import java.time.YearMonth

data class EventState (
    val isTodayEventLoading: Boolean = true,
    val isDatedEventLoading: Boolean = true,
    val isAllEventsLoading: Boolean = true,
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val allEvent: List<EventModel> = emptyList(),
    val todayEvents: List<EventModel> = emptyList(),
    val datedEvents: List<EventModel> = emptyList(),
    val allEventInstances: List<EventInstanceModel> = emptyList()
)
