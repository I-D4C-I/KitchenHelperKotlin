package com.example.kitchenhelperkotlin.recipe.addedit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentAddEditRecipeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddEditRecipeFragment : Fragment(R.layout.fragment_add_edit_recipe) {

    @Inject
    lateinit var factory: AddEditRecipeViewModel.AddEditFactory

    private val viewModel: AddEditRecipeViewModel by viewModels {
        AddEditRecipeViewModel.provideFactory(factory, this, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditRecipeBinding.bind(view)

        binding.apply {
            eRecipeTitle.setText(viewModel.recipeTitle)
            cbFavorite.isChecked = viewModel.recipeFavorite
            cbFavorite.jumpDrawablesToCurrentState()
            eRecipeDescription.setText(viewModel.recipeNote)

        }
    }

}