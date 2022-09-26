package com.example.kitchenhelperkotlin.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kitchenhelperkotlin.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.DEFAULT)

    private val recipeFlow = combine(
        searchQuery, sortOrder
    ) { query, sortOrder ->
        Pair(query, sortOrder)
    }.flatMapLatest { (query, sortOrder) ->
            recipeDao.getRecipes(query, sortOrder)
    }

    val recipes = recipeFlow.asLiveData()

}