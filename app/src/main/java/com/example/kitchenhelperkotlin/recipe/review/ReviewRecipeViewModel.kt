package com.example.kitchenhelperkotlin.recipe.review

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.events.recipeEvents.RecipeReviewEvent
import com.example.kitchenhelperkotlin.recipe.Recipe
import com.example.kitchenhelperkotlin.recipe.RecipeDao
import com.example.kitchenhelperkotlin.util.ADD_RESULT_OK
import com.example.kitchenhelperkotlin.util.EDIT_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

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

    private val viewRecipeEventChannel = Channel<RecipeReviewEvent>()
    val viewRecipeEvent = viewRecipeEventChannel.receiveAsFlow()

    fun onEditClick() {
        editRecipe(recipe)
    }

    fun onEditResult(result: Int) {
        when (result) {
            ADD_RESULT_OK -> showConfirmationMessage(
                getApplication<Application>().resources.getString(
                    R.string.addedRecipe
                )
            )
            EDIT_RESULT_OK -> showConfirmationMessage(
                getApplication<Application>().resources.getString(
                    R.string.editedRecipe
                )
            )
        }
    }

    private fun editRecipe(recipe: Recipe) = viewModelScope.launch{
        viewRecipeEventChannel.send(RecipeReviewEvent.NavigateToEditRecipeScreen(recipe))
    }

    private fun showConfirmationMessage(text: String) = viewModelScope.launch {
        viewRecipeEventChannel.send(RecipeReviewEvent.ShowSavedConfirmationMessage(text))
    }


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