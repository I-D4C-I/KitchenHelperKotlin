package com.example.kitchenhelperkotlin.recipe.addedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.events.recipeEvents.RecipeAddEditEvent
import com.example.kitchenhelperkotlin.recipe.Recipe
import com.example.kitchenhelperkotlin.recipe.RecipeDao
import com.example.kitchenhelperkotlin.util.ADD_RESULT_OK
import com.example.kitchenhelperkotlin.util.EDIT_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditRecipeViewModel @AssistedInject constructor(
    application: Application,
    private val recipeDao: RecipeDao,
    @Assisted private val state: SavedStateHandle
) : AndroidViewModel(application) {

    var currentPart = 0

    val recipe = state.get<Recipe>("recipe")

    var recipeTitle = state.get<String>("recipeTitle") ?: recipe?.title ?: ""
        set(value) {
            field = value
            state["recipeTitle"] = value
        }

    var recipeFavorite = state.get<Boolean>("recipeFavorite") ?: recipe?.favorite ?: false
        set(value) {
            field = value
            state["recipeFavorite"] = value
        }

    var recipeNote = state.get<String>("recipeNote") ?: recipe?.note ?: ""
        set(value) {
            field = value
            state["recipeNote"] = value
        }

    private var recipeDescription =
        state.get<ArrayList<String>>("recipeDesc") ?: recipe?.description ?: arrayListOf("null")
        set(value) {
            field = value
            state["recipeDesc"] = value
        }

    var recipePart = state.get<String>("recipePart") ?: recipe?.description?.get(currentPart) ?: ""
        set(value) {
            field = value
            state["recipePart"] = value
        }

    private val addEditRecipeEventChannel = Channel<RecipeAddEditEvent>()
    val addEditRecipeEvent = addEditRecipeEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (!areMainFieldsCorrect())
            return
        recipeDescription[currentPart] = recipePart
        if (recipe != null) {
            val updatedRecipe =
                recipe.copy(
                    title = recipeTitle,
                    favorite = recipeFavorite,
                    note = recipeNote,
                    description = recipeDescription
                )
            updateRecipe(updatedRecipe)
        } else {
            val newRecipe =
                Recipe(
                    title = recipeTitle,
                    favorite = recipeFavorite,
                    note = recipeNote,
                    description = recipeDescription
                )
            createRecipe(newRecipe)
        }
    }

    private fun areMainFieldsCorrect(): Boolean {
        if (recipeTitle.isBlank() || recipePart.isBlank()) {
            showInvalidMessage(getApplication<Application>().resources.getString(R.string.retype))
            return false
        }
        return true
    }

    fun onNextClick() {
        if (!areMainFieldsCorrect())
            return
        recipeDescription[currentPart] = recipePart
        currentPart += 1
        if (recipeDescription.size == currentPart)
            recipeDescription.add("")
        showNewPart()
    }

    fun onPreciousClick() {
        recipeDescription[currentPart] = recipePart
        currentPart -= 1
        if (currentPart < 0)
            currentPart = 0
        showNewPart()
    }

    fun onDeleteClick() {
        if (recipeDescription.size == 1)
            recipeDescription[currentPart] = ""
        else {
            recipeDescription.removeAt(currentPart)
            if (recipeDescription.size == currentPart)
                currentPart -= 1
        }
        showNewPart()
    }

    private fun showInvalidMessage(text: String) = viewModelScope.launch {
        addEditRecipeEventChannel.send(RecipeAddEditEvent.ShowInvalidInputMessage(text))
    }

    private fun createRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.insert(recipe)
        addEditRecipeEventChannel.send(RecipeAddEditEvent.NavigateBackWithResult(ADD_RESULT_OK))
    }

    private fun updateRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.update(recipe)
        addEditRecipeEventChannel.send(RecipeAddEditEvent.NavigateBackWithResult(EDIT_RESULT_OK))
    }

    private fun showNewPart() = viewModelScope.launch {
        recipePart = recipeDescription[currentPart]
        addEditRecipeEventChannel.send(RecipeAddEditEvent.ShowNewPart)
    }

    @AssistedFactory
    interface AddEditFactory {
        fun create(handle: SavedStateHandle): AddEditRecipeViewModel
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