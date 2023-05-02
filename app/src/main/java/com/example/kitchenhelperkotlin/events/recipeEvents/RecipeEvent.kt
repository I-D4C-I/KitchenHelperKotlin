package com.example.kitchenhelperkotlin.events.recipeEvents

import com.example.kitchenhelperkotlin.recipe.Recipe

sealed class RecipeEvent{
    object NavigateToAddRecipeScreen : RecipeEvent()
    data class NavigateToViewRecipeScreen(val recipe: Recipe) : RecipeEvent()
    data class ShowUndoDeleteMessage(val recipe : Recipe) : RecipeEvent()
    data class ShowSavedConfirmationMessage(val message: String) : RecipeEvent()
}
