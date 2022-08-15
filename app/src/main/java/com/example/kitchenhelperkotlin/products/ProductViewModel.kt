package com.example.kitchenhelperkotlin.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kitchenhelperkotlin.PreferencesRepository
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.events.ProductEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    application: Application,
    private val productDao: ProductDao,
    private val preferencesRepository: PreferencesRepository
) : AndroidViewModel(application) {

    val searchQuery = MutableStateFlow("")

    private val preferencesFlow = preferencesRepository.preferencesProductFlow

    private val productsEventChannel = Channel<ProductEvent>()
    val productEvent = productsEventChannel.receiveAsFlow()

    private val productsFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        productDao.getProducts(query, filterPreferences.sortOrder)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesRepository.updateProductSortOrder(sortOrder)
    }

    val products = productsFlow.asLiveData()

    fun onProductSelected(product: Product) {

    }

    fun onProductSwiped(product: Product) = viewModelScope.launch {
        productDao.delete(product)
        productsEventChannel.send(ProductEvent.ShowUndoDeleteMessage(product))
    }

    fun onUndoDeleteClick(product: Product) = viewModelScope.launch {
        productDao.insert(product)
    }

}