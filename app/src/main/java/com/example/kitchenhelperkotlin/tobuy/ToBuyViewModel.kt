package com.example.kitchenhelperkotlin.tobuy

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
class ToBuyViewModel @Inject constructor(
    private val toBuyDao: ToBuyDao,
    private val preferences : PreferencesRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferences.preferencesFlow

    private val toBuyFlow = combine(
        searchQuery,
        preferencesFlow
    ){
        query, filterPreferences ->
        Pair(query,filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        toBuyDao.getToBuy(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferences.updateSortOrder(sortOrder)
    }

    fun onCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferences.updateHideCompleted(hideCompleted)
    }

    /*
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)
    */

    /*
    private val toBuyFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ){
        query, sortOrder, hideCompleted ->
        Triple(query,sortOrder,hideCompleted) //Имеется Pair для двух
    }.flatMapLatest { (query, sortOrder, hideCompleted) ->
        toBuyDao.getToBuy(query, sortOrder, hideCompleted)
    }
*/
    val toBuys = toBuyFlow.asLiveData()
}

