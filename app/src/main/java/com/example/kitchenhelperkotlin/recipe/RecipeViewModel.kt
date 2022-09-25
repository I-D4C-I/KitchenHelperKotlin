package com.example.kitchenhelperkotlin.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val recipeFlow = searchQuery.flatMapLatest {
        recipeDao.getRecipes(it)
    }

    val recipes = recipeFlow.asLiveData()


}