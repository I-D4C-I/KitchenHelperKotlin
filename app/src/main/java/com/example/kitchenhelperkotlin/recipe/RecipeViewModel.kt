package com.example.kitchenhelperkotlin.recipe

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.PreferencesRepository
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.events.recipeEvents.RecipeEvent
import com.example.kitchenhelperkotlin.util.ADD_RESULT_OK
import com.example.kitchenhelperkotlin.util.EDIT_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RecipeViewModel @AssistedInject constructor(
    application: Application,
    private val recipeDao: RecipeDao,
    private val preferencesRepository: PreferencesRepository,
    @Assisted private val state: SavedStateHandle
) : AndroidViewModel(application) {

    val searchQuery = state.getLiveData("searchQuery", "")

    private val preferencesFlow = preferencesRepository.preferencesRecipeFlow

    private val recipeEventChannel = Channel<RecipeEvent>()
    val recipeEvent = recipeEventChannel.receiveAsFlow()

    private val recipeFlow = combine(
        searchQuery.asFlow(), preferencesFlow
    ) { query, preferencesFlow ->
        Pair(query, preferencesFlow)
    }.flatMapLatest { (query, preferencesFlow) ->
        recipeDao.getRecipes(query, preferencesFlow.sortOrder)
    }

    val recipes = recipeFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesRepository.updateRecipeSortOrder(sortOrder)
    }

    fun onRecipeSelected(recipe: Recipe) = viewModelScope.launch {
        recipeEventChannel.send(RecipeEvent.NavigateToViewRecipeScreen(recipe))
    }

    fun onRecipeSwiped(recipe: Recipe) = viewModelScope.launch {
        recipeDao.delete(recipe)
        recipeEventChannel.send(RecipeEvent.ShowUndoDeleteMessage(recipe))
    }

    fun onUndoDeleteClick(recipe: Recipe) = viewModelScope.launch {
        recipeDao.insert(recipe)
    }

    fun onAddNewRecipeClick() = viewModelScope.launch {
        recipeEventChannel.send(RecipeEvent.NavigateToAddRecipeScreen)
    }

    fun onAddEditResult(result: Int) {
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

    private fun showConfirmationMessage(text: String) = viewModelScope.launch {
        recipeEventChannel.send(RecipeEvent.ShowSavedConfirmationMessage(text))
    }


    @AssistedFactory
    interface ProductModelFactory {
        fun create(handle: SavedStateHandle): RecipeViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: ProductModelFactory,
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