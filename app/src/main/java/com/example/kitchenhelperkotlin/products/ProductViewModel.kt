package com.example.kitchenhelperkotlin.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    application: Application,
    private val productDao: ProductDao
) : AndroidViewModel(application) {

    val searchQuery = MutableStateFlow("")

    private val productsFlow = searchQuery.flatMapLatest {
        productDao.getProducts(it)
    }

    val products = productsFlow.asLiveData()

}