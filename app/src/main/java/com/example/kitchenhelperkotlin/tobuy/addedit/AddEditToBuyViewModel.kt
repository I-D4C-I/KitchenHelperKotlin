package com.example.kitchenhelperkotlin.tobuy.addedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.tobuy.ToBuy
import com.example.kitchenhelperkotlin.tobuy.ToBuyDao
import com.example.kitchenhelperkotlin.util.ADD_RESULT_OK
import com.example.kitchenhelperkotlin.events.AddEditEvent
import com.example.kitchenhelperkotlin.util.EDIT_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditToBuyViewModel @AssistedInject constructor(
    application: Application,
    private val toBuyDao: ToBuyDao,
    @Assisted val stateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    var productToBuy = stateHandle.get<String>("productTitle")

    val toBuy = stateHandle.get<ToBuy>("toBuy")

    var toBuyTitle = stateHandle.get<String>("toBuyTitle") ?: toBuy?.title ?: productToBuy ?: ""
        set(value) {
            field = value
            stateHandle["toBuyTitle"] = value
        }
    var toBuyAmount = stateHandle.get<String>("toBuyAmount") ?: toBuy?.amount.toString()
        set(value) {
            field = value
            stateHandle["toBuyAmount"] = value
        }
    var toBuyImportance = stateHandle.get<Boolean>("toBuyImportance") ?: toBuy?.important ?: false
        set(value) {
            field = value
            stateHandle["toBuyImportance"] = value
        }

    private val addEditToBuyEventChannel = Channel<AddEditEvent>()
    val addEditToBuyEvent = addEditToBuyEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (toBuyTitle.isBlank() || (toBuyAmount.isBlank() || toBuyAmount == "null") ) {
            showInvalidInputMessage(getApplication<Application>().resources.getString(R.string.retype))
            return
        }
        if (toBuy != null) {
            val updatedToBuy = toBuy.copy(
                title = toBuyTitle,
                amount = toBuyAmount.toInt(),
                important = toBuyImportance
            )
            updatedToBuy(updatedToBuy)
        } else {
            val newToBuy =
                ToBuy(title = toBuyTitle, amount = toBuyAmount.toInt(), important = toBuyImportance)
            createToBuy(newToBuy)
        }
    }

    private fun showInvalidInputMessage(message: String) = viewModelScope.launch {
        addEditToBuyEventChannel.send(AddEditEvent.ShowInvalidInputMessage(message))
    }

    private fun createToBuy(createToBuy: ToBuy) = viewModelScope.launch {
        toBuyDao.insert(createToBuy)
        addEditToBuyEventChannel.send(AddEditEvent.NavigateBackWithResult(ADD_RESULT_OK))
    }

    private fun updatedToBuy(updatedToBuy: ToBuy) = viewModelScope.launch {
        toBuyDao.update(updatedToBuy)
        addEditToBuyEventChannel.send(AddEditEvent.NavigateBackWithResult(EDIT_RESULT_OK))
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