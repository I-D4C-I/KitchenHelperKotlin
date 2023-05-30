package com.example.kitchenhelperkotlin.products.addedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.events.AddEditEvent
import com.example.kitchenhelperkotlin.products.Product
import com.example.kitchenhelperkotlin.products.ProductDao
import com.example.kitchenhelperkotlin.products.UnitOfMeasure.Measure
import com.example.kitchenhelperkotlin.util.ADD_RESULT_OK
import com.example.kitchenhelperkotlin.util.EDIT_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddEditProductViewModel @AssistedInject constructor(
    application: Application,
    private val productDao: ProductDao,
    @Assisted private val stateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val product = stateHandle.get<Product>("product")

    var productTitle = stateHandle.get<String>("productTitle") ?: product?.title ?: ""
        set(value) {
            field = value
            stateHandle["productTitle"] = value
        }

    var productAmount = stateHandle.get<String>("productAmount") ?: product?.amount.toString()
        set(value) {
            field = value
            stateHandle["productAmount"] = value
        }

    var productDate: LocalDate = stateHandle.get<LocalDate>("productDate") ?: product?.date ?: LocalDate.now()
        set(value) {
            field = value
            stateHandle["productDate"] = value
        }

    var productMeasure =
        stateHandle.get<Measure>("productMeasure") ?: product?.measure ?: Measure.kg

    private val addEditProductEventChannel = Channel<AddEditEvent>()
    val addEditProductEvent = addEditProductEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (productTitle.isBlank() || (productAmount.isBlank() || productAmount == "null")) {
            showInvalidInputMessage(getApplication<Application>().resources.getString(R.string.retype))
            return
        }
        if (product != null) {
            val updatedProduct = product.copy(
                title = productTitle,
                amount = productAmount.toInt(),
                date = productDate,
                measure = productMeasure
            )
            updateProduct(updatedProduct)
        } else {
            val newProduct =
                Product(
                    title = productTitle,
                    amount = productAmount.toInt(),
                    date = productDate,
                    measure = productMeasure
                )
            createProduct(newProduct)
        }
    }

    private fun createProduct(newProduct: Product) = viewModelScope.launch {
        productDao.insert(newProduct)
        addEditProductEventChannel.send(AddEditEvent.NavigateBackWithResult(ADD_RESULT_OK))
    }

    private fun updateProduct(updatedProduct: Product) = viewModelScope.launch {
        productDao.update(updatedProduct)
        addEditProductEventChannel.send(AddEditEvent.NavigateBackWithResult(EDIT_RESULT_OK))
    }

    private fun showInvalidInputMessage(message: String) = viewModelScope.launch {
        addEditProductEventChannel.send(AddEditEvent.ShowInvalidInputMessage(message))
    }

    @AssistedFactory
    interface AddEditFactory {
        fun create(handle: SavedStateHandle): AddEditProductViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AddEditFactory,
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