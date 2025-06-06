package com.flux.ui.effects

sealed class ScreenEffect {
    data class ShowSnackBarMessage(val message: String) : ScreenEffect()
}