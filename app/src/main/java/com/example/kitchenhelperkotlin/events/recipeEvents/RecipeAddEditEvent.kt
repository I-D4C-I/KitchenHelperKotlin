package com.example.kitchenhelperkotlin.events.recipeEvents

sealed class RecipeAddEditEvent {
    data class ShowInvalidInputMessage(val msg: String) : RecipeAddEditEvent()
    data class NavigateBackWithResult(val result: Int) : RecipeAddEditEvent()
    object ShowNewPart : RecipeAddEditEvent()
}