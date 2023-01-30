package com.example.kitchenhelperkotlin.events

import com.example.kitchenhelperkotlin.recipe.Recipe

sealed class RecipeEvent{
    data class ShowUndoDeleteMessage(val recipe : Recipe) : RecipeEvent()
}
