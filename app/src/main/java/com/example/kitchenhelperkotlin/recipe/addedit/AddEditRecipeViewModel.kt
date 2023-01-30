package com.example.kitchenhelperkotlin.recipe.addedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.recipe.Recipe
import com.example.kitchenhelperkotlin.recipe.RecipeDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AddEditRecipeViewModel @AssistedInject constructor(
    application: Application,
    private val recipeDao: RecipeDao,
    @Assisted private val state: SavedStateHandle
) : AndroidViewModel(application) {

    val recipe = state.get<Recipe>("recipe")

    var recipeTitle = state.get<String>("recipeTitle") ?: recipe?.title ?: ""
        set(value) {
            field = value
            state["recipeTitle"] = value
        }

    var recipeFavorite = state.get<Boolean>("recipeFavorite") ?: recipe?.favorite ?: false
        set(value) {
            field = value
            state["recipeFavorite"] = value
        }

    var recipeNote = state.get<String>("recipeNote") ?: recipe?.note ?: ""
        set(value) {
            field = value
            state["recipeNote"] = value
        }

    @AssistedFactory
    interface AddEditFactory {
        fun create(handle: SavedStateHandle): AddEditRecipeViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AddEditFactory,
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