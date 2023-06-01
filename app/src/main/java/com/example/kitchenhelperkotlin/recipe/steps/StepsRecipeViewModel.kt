package com.example.kitchenhelperkotlin.recipe.steps

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.events.recipeEvents.RecipeAddEditEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StepsRecipeViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val state: SavedStateHandle
) : AndroidViewModel(application) {

    var numberOfStep = 0

    private val stepsRecipe = state.get<ArrayList<String>>("steps") ?: arrayListOf(
        getApplication<Application>().resources.getString(
            R.string.errorDescription
        )
    )
    var step = stepsRecipe[numberOfStep]

    private val stepsRecipeEventChannel = Channel<RecipeAddEditEvent>()
    val stepsRecipeEvent = stepsRecipeEventChannel.receiveAsFlow()


    fun onNextClick() {
        numberOfStep++
        if (numberOfStep + 1 > stepsRecipe.size)
            numberOfStep = stepsRecipe.size
        showNewStep()
    }

    fun onPreciousClick() {
        numberOfStep--
        if (numberOfStep < 0)
            numberOfStep = 0
        showNewStep()
    }

    private fun showNewStep() = viewModelScope.launch {
        step = stepsRecipe[numberOfStep]
        stepsRecipeEventChannel.send(RecipeAddEditEvent.ShowNewPart)
    }


    @AssistedFactory
    interface ViewFactory {
        fun create(handle: SavedStateHandle): StepsRecipeViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: ViewFactory,
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