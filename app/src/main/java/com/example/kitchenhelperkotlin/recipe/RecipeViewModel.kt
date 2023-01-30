package com.example.kitchenhelperkotlin.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kitchenhelperkotlin.PreferencesRepository
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.events.RecipeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    private val preferencesFlow = preferencesRepository.preferencesRecipeFlow

    private val recipeEventChannel = Channel<RecipeEvent>()
    val recipeEvent = recipeEventChannel.receiveAsFlow()

    private val recipeFlow = combine(
        searchQuery, preferencesFlow
    ) { query, preferencesFlow ->
        Pair(query, preferencesFlow)
    }.flatMapLatest { (query, preferencesFlow) ->
        recipeDao.getRecipes(query, preferencesFlow.sortOrder)
    }

    val recipes = recipeFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesRepository.updateRecipeSortOrder(sortOrder)
    }

    fun onRecipeSelected(recipe: Recipe) {

    }

    fun onRecipeSwiped(recipe: Recipe) = viewModelScope.launch {
        recipeDao.delete(recipe)
        recipeEventChannel.send(RecipeEvent.ShowUndoDeleteMessage(recipe))
    }

    fun onUndoDeleteClick(recipe: Recipe) = viewModelScope.launch {
        recipeDao.insert(recipe)
    }
}