package com.flux.other

sealed class EditAction {
    data class TitleChanged(val old: String, val new: String) : EditAction()
    data class DescriptionChanged(val old: String, val new: String) : EditAction()
}
