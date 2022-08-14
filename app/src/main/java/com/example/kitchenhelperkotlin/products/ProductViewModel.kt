package com.example.kitchenhelperkotlin.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.kitchenhelperkotlin.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    application: Application,
    private val productDao: ProductDao
) : AndroidViewModel(application) {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.DEFAULT)

    private val productsFlow = combine(
        searchQuery,
        sortOrder
    ) { query, sortOrder ->
        Pair(query, sortOrder)
    }.flatMapLatest { (query, sortOrder) ->
        productDao.getProducts(query, sortOrder)
    }

    val products = productsFlow.asLiveData()

}