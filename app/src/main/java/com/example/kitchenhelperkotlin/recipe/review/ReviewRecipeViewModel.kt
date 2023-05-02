package com.example.kitchenhelperkotlin.recipe.review

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.recipe.Recipe
import com.example.kitchenhelperkotlin.recipe.RecipeDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ReviewRecipeViewModel @AssistedInject constructor(
    application: Application,
    recipeDao: RecipeDao,
    @Assisted private val state: SavedStateHandle
) : AndroidViewModel(application) {

    private val recipe = state.get<Recipe>("recipe") ?: Recipe(
        title = "Error", description = arrayListOf(
            getApplication<Application>().resources.getString(
                R.string.errorDescription
            )
        )
    )

    val obsRecipe = recipeDao.getRecipeById(recipe.id).asLiveData()

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