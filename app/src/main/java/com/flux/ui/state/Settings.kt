package com.flux.ui.state

import com.flux.data.model.SettingsModel

data class Settings(
    val isLoading: Boolean = true,
    val data: SettingsModel = SettingsModel()
)
