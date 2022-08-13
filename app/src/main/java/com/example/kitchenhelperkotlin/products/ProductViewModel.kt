package com.example.kitchenhelperkotlin.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    application: Application,
    private val productDao: ProductDao
) : AndroidViewModel(application) {
}