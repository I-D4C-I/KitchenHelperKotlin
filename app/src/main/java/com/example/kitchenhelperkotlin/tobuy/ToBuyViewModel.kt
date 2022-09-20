package com.example.kitchenhelperkotlin.tobuy

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.kitchenhelperkotlin.PreferencesRepository
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.events.ToBuyEvent
import com.example.kitchenhelperkotlin.util.ADD_RESULT_OK
import com.example.kitchenhelperkotlin.util.EDIT_RESULT_OK
import com.example.kitchenhelperkotlin.util.NotificationWorker
import com.example.kitchenhelperkotlin.util.TOBUY_NOTIFICATION
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ToBuyViewModel @AssistedInject constructor(
    application: Application,
    private val toBuyDao: ToBuyDao,
    private val preferences: PreferencesRepository,
    @Assisted val savedState: SavedStateHandle
) : AndroidViewModel(application) {

    val searchQuery = savedState.getLiveData("searchQuery", "")

    val preferencesFlow = preferences.preferencesFlow

    private val toBuyEventChannel = Channel<ToBuyEvent>()
    val toBuyEvent = toBuyEventChannel.receiveAsFlow()

    private val workManager = WorkManager.getInstance(application)

    private val toBuyFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        toBuyDao.getToBuy(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferences.updateToBuySortOrder(sortOrder)
    }

    fun onCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferences.updateToBuyHideCompleted(hideCompleted)
    }

    val toBuys = toBuyFlow.asLiveData()

    fun onToBuySelected(toBuy: ToBuy) = viewModelScope.launch {
        toBuyEventChannel.send(ToBuyEvent.NavigateToEditScreen(toBuy))
    }

    fun onToBuyCheckedChanged(toBuy: ToBuy, isChecked: Boolean) = viewModelScope.launch {
        toBuyDao.update(toBuy.copy(completed = isChecked))
    }

    fun onToBuySwiped(toBuy: ToBuy) = viewModelScope.launch {
        toBuyDao.delete(toBuy)
        toBuyEventChannel.send(ToBuyEvent.ShowUndoDeleteMessage(toBuy))
    }

    fun onUndoDeleteClick(toBuy: ToBuy) = viewModelScope.launch {
        toBuyDao.insert(toBuy)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        toBuyEventChannel.send(ToBuyEvent.NavigateToAddScreen)
    }

    fun onCreateNotificationClick() = viewModelScope.launch {
        toBuyEventChannel.send(ToBuyEvent.ShowCreateNotificationBottomSheet)
    }

    fun createNotification(delayInSeconds: Long) = viewModelScope.launch {

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    "title" to getApplication<Application>().resources.getString(R.string.notificationTitle),
                    "description" to getApplication<Application>().resources.getString(R.string.notificationDescription),
                    "notificationId" to TOBUY_NOTIFICATION
                )
            )
            .build()
        workManager.beginUniqueWork("TOBUY_NOTIFICATION", ExistingWorkPolicy.REPLACE, workRequest)
            .enqueue()

        toBuyEventChannel.send(ToBuyEvent.ShowCreateNotificationMessage("TOBUY_NOTIFICATION"))
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_RESULT_OK -> showConfirmationMessage(
                getApplication<Application>().resources.getString(
                    R.string.addedToBuy
                )
            )
            EDIT_RESULT_OK -> showConfirmationMessage(
                getApplication<Application>().resources.getString(
                    R.string.editedToBuy
                )
            )
        }
    }

    private fun showConfirmationMessage(message: String) = viewModelScope.launch {
        toBuyEventChannel.send(ToBuyEvent.ShowConfirmationMessage(message))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        toBuyEventChannel.send(ToBuyEvent.NavigateToDeleteAllScreen)
    }

    fun onCancelNotificationClick(tag: String) = viewModelScope.launch {
        workManager.cancelUniqueWork(tag)
    }

    @AssistedFactory
    interface ToBuyModelFactory {
        fun create(handle: SavedStateHandle): ToBuyViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: ToBuyModelFactory,
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
/*
val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
val hideCompleted = MutableStateFlow(false)
*/

/*
private val toBuyFlow = combine(
    searchQuery,
    sortOrder,
    hideCompleted
){
    query, sortOrder, hideCompleted ->
    Triple(query,sortOrder,hideCompleted) //Имеется Pair для двух
}.flatMapLatest { (query, sortOrder, hideCompleted) ->
    toBuyDao.getToBuy(query, sortOrder, hideCompleted)
}
*/



