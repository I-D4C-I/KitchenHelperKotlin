package com.example.kitchenhelperkotlin.recipe.review

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.recipe.Recipe
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ReviewRecipeViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val state: SavedStateHandle
) : AndroidViewModel(application) {

    val recipe = state.get<Recipe>("recipe")

    var recipeTitle = state.get<String>("recipeTitle") ?: recipe?.title ?: ""


    @AssistedFactory
    interface ViewFactory {
        fun create(handle: SavedStateHandle): ReviewRecipeViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: ViewFactory,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return assistedFactory.create(handle) as T
                }
            }
    }
}