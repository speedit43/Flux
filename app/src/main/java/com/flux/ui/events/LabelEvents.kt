package com.flux.ui.events

import com.flux.data.model.LabelModel

sealed class LabelEvents {
    data class UpsertLabel(val label: LabelModel): LabelEvents()
    data class DeleteLabel(val label: LabelModel): LabelEvents()
}