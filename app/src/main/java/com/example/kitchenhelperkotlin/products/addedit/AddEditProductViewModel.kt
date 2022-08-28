package com.example.kitchenhelperkotlin.products.addedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.products.Product
import com.example.kitchenhelperkotlin.products.ProductDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

    var productDate = stateHandle.get<LocalDate>("productDate") ?: product?.date
        set(value) {
            field = value
            stateHandle["productDate"] = value
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