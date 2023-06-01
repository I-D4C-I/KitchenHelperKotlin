package com.example.kitchenhelperkotlin.recipe.steps

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class StepsRecipeViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val state: SavedStateHandle
) : AndroidViewModel(application) {

    var numberOfStep = 0

    private val stepsRecipe = state.get<ArrayList<String>>("steps") ?: arrayListOf(
        getApplication<Application>().resources.getString(
            R.string.errorDescription)
    )
    var step = stepsRecipe[numberOfStep]



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