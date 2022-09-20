package com.example.kitchenhelperkotlin.dialog


import androidx.lifecycle.ViewModel
import com.example.kitchenhelperkotlin.dependencyinjection.ApplicationScope
import com.example.kitchenhelperkotlin.products.ProductDao
import com.example.kitchenhelperkotlin.tobuy.ToBuyDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllViewModel @Inject constructor(
    private val toBuyDao: ToBuyDao,
    private val productDao: ProductDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmDeleteCompletedClick() = applicationScope.launch {
        toBuyDao.deleteCompletedToBuy()
    }

    fun onConfirmDeleteProductsClick() = applicationScope.launch {
        productDao.deleteAllProducts()
    }

    fun onConfirmDeleteToBuyClick() = applicationScope.launch {
        toBuyDao.deleteAllToBuy()
    }
}