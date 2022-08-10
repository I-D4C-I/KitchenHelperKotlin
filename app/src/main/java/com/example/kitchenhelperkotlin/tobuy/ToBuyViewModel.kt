package com.example.kitchenhelperkotlin.tobuy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kitchenhelperkotlin.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ToBuyViewModel @Inject constructor(
    private val toBuyDao: ToBuyDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

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

    val toBuys = toBuyFlow.asLiveData()
}

