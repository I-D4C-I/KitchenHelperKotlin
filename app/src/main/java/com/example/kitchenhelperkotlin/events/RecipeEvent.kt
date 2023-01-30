package com.example.kitchenhelperkotlin.events

import com.example.kitchenhelperkotlin.recipe.Recipe

sealed class RecipeEvent{
    object NavigateToAddRecipeScreen : RecipeEvent()
    data class NavigateToEditRecipeScreen(val recipe: Recipe) : RecipeEvent()
    data class ShowUndoDeleteMessage(val recipe : Recipe) : RecipeEvent()
}
