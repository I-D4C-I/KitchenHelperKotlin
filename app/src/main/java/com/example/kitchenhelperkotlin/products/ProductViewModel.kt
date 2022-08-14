package com.example.kitchenhelperkotlin.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    application: Application,
    private val productDao: ProductDao
) : AndroidViewModel(application) {

    val products = productDao.getProducts().asLiveData()

}