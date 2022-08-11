package com.example.kitchenhelperkotlin.tobuy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kitchenhelperkotlin.PreferencesRepository
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.util.ItemEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToBuyViewModel @Inject constructor(
    private val toBuyDao: ToBuyDao,
    private val preferences : PreferencesRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferences.preferencesFlow

    private val toBuyEventChannel = Channel<ItemEvent>()
    val toBuyEvent = toBuyEventChannel.receiveAsFlow()

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

    val toBuys = toBuyFlow.asLiveData()

    fun onToBuySelected(toBuy: ToBuy) {
        TODO("Not yet implemented")
    }

    fun onToBuyCheckedChanged(toBuy: ToBuy, isChecked: Boolean) = viewModelScope.launch {
        toBuyDao.update(toBuy.copy(completed = isChecked))
    }

    fun onToBuySwiped(toBuy: ToBuy) = viewModelScope.launch {
        toBuyDao.delete(toBuy)
        toBuyEventChannel.send(ItemEvent.ShowUndoDeleteToBuyMessage(toBuy))
    }

    fun onUndoDeleteClick(toBuy: ToBuy) = viewModelScope.launch {
        toBuyDao.insert(toBuy)
    }
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



