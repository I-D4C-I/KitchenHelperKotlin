package com.example.kitchenhelperkotlin.events.recipeEvents

import com.example.kitchenhelperkotlin.recipe.Recipe

sealed class RecipeReviewEvent {
    data class NavigateToEditRecipeScreen(val recipe: Recipe) : RecipeReviewEvent()
    data class NavigateToStepsRecipeScreen(val recipe: Recipe) : RecipeReviewEvent()
    data class ShowSavedConfirmationMessage(val text: String) : RecipeReviewEvent()
}
