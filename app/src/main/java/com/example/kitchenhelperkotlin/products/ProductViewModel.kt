package com.example.kitchenhelperkotlin.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
class ProductViewModel @Inject constructor(
    application: Application,
    private val productDao: ProductDao,
    private val preferencesRepository: PreferencesRepository
) : AndroidViewModel(application) {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesRepository.preferencesProductFlow

    private val productsFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        productDao.getProducts(query, filterPreferences.sortOrder)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch{
        preferencesRepository.updateProductSortOrder(sortOrder)
    }

    val products = productsFlow.asLiveData()

}