package com.example.kitchenhelperkotlin.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kitchenhelperkotlin.PreferencesRepository
import com.example.kitchenhelperkotlin.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val preferencesFlow = preferencesRepository.preferencesRecipeFlow

    private val recipeFlow = combine(
        searchQuery, preferencesFlow
    ) { query, preferencesFlow ->
        Pair(query, preferencesFlow)
    }.flatMapLatest { (query, preferencesFlow) ->
        recipeDao.getRecipes(query, preferencesFlow.sortOrder)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesRepository.updateRecipeSortOrder(sortOrder)
    }

    val recipes = recipeFlow.asLiveData()

}