package com.example.kitchenhelperkotlin.tobuy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ToBuyViewModel @Inject constructor(
    private val toBuyDao: ToBuyDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val toBuyFlow = searchQuery.flatMapLatest {
        toBuyDao.getToBuy(it)
    }

    val toBuys = toBuyFlow.asLiveData()
}