package com.example.kitchenhelperkotlin.events

sealed class RecipeAddEditEvent {
    data class ShowInvalidInputMessage(val msg: String) : RecipeAddEditEvent()
    data class NavigateBackWithResult(val result: Int) : RecipeAddEditEvent()
    object ShowNextPart : RecipeAddEditEvent()
    object ShowPreciousPart : RecipeAddEditEvent()
}