package com.example.kitchenhelperkotlin.products

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.PreferencesRepository
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.events.ProductEvent
import com.example.kitchenhelperkotlin.util.ADD_RESULT_OK
import com.example.kitchenhelperkotlin.util.EDIT_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class ProductViewModel @AssistedInject constructor(
    application: Application,
    private val productDao: ProductDao,
    private val preferencesRepository: PreferencesRepository,
    @Assisted private val stateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val searchQuery = stateHandle.getLiveData("searchQuery", "")

    private val preferencesFlow = preferencesRepository.preferencesProductFlow

    private val productsEventChannel = Channel<ProductEvent>()
    val productEvent = productsEventChannel.receiveAsFlow()

    private val productsFlow = combine(
        searchQuery.asFlow(),
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

    fun onProductSelected(product: Product) = viewModelScope.launch {
        productsEventChannel.send(ProductEvent.NavigateToEditProductScreen(product))
    }

    fun onProductSwiped(product: Product) = viewModelScope.launch {
        productDao.delete(product)
        productsEventChannel.send(ProductEvent.ShowUndoDeleteMessage(product))
    }

    fun onUndoDeleteClick(product: Product) = viewModelScope.launch {
        productDao.insert(product)
    }

    fun addNewProductClick() = viewModelScope.launch {
        productsEventChannel.send(ProductEvent.NavigateToAddProductScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_RESULT_OK -> showConfirmationMessage(
                getApplication<Application>().resources.getString(
                    R.string.addedProduct
                )
            )
            EDIT_RESULT_OK -> showConfirmationMessage(
                getApplication<Application>().resources.getString(
                    R.string.editedProduct
                )
            )
        }
    }

    private fun showConfirmationMessage(message: String) = viewModelScope.launch {
        productsEventChannel.send(ProductEvent.ShowSavedConfirmationMessage(message))
    }

    fun swapData(productFrom: Product, productTo: Product) = viewModelScope.launch {

        var updatedProduct = productFrom.copy(
            id = productTo.id
        )
        productDao.update(updatedProduct)
        updatedProduct = productTo.copy(
            id = productFrom.id
        )
        productDao.update(updatedProduct)
    }


    @AssistedFactory
    interface ProductModelFactory {
        fun create(handle: SavedStateHandle): ProductViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: ProductModelFactory,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return assistedFactory.create(handle) as T
                }
            }
    }

}