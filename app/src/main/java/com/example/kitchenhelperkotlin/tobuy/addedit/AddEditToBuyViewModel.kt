package com.example.kitchenhelperkotlin.tobuy.addedit

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.tobuy.ToBuy
import com.example.kitchenhelperkotlin.tobuy.ToBuyDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AddEditToBuyViewModel @AssistedInject constructor(
    private val toBuyDao: ToBuyDao,
    @Assisted val stateHandle: SavedStateHandle
) : ViewModel() {

    val toBuy = stateHandle.get<ToBuy>("toBuy")

    var toBuyTitle = stateHandle.get<String>("toBuyTitle") ?: toBuy?.title ?: ""
        set(value) {
            field = value
            stateHandle["toBuyTitle"] = value
        }
    var toBuyAmount = stateHandle.get<Int>("toBuyAmount") ?: toBuy?.amount ?: 0
        set(value) {
            field = value
            stateHandle["toBuyAmount"] = value
        }
    var toBuyImportance = stateHandle.get<Boolean>("toBuyImportance") ?: toBuy?.important ?: false
        set(value) {
            field = value
            stateHandle["toBuyImportance"] = value
        }


    @AssistedFactory
    interface AddEditFactory {
        fun create(handle: SavedStateHandle): AddEditToBuyViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AddEditFactory,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return assistedFactory.create(handle) as T
                }
            }
    }


}