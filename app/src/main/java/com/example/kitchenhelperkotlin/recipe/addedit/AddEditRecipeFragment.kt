package com.example.kitchenhelperkotlin.recipe.addedit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentAddEditRecipeBinding
import com.example.kitchenhelperkotlin.events.recipeEvents.RecipeAddEditEvent
import com.example.kitchenhelperkotlin.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("IMPLICIT_CAST_TO_ANY", "DEPRECATION")
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

        fun refreshPart() {
            binding.eRecipeDescription.clearFocus()
            binding.vPart.text = getString(R.string.part, viewModel.currentPart + 1)
            binding.eRecipeDescription.setText(viewModel.recipePart)
        }

        binding.apply {
            eRecipeTitle.setText(viewModel.recipeTitle)
            cbFavorite.isChecked = viewModel.recipeFavorite
            cbFavorite.jumpDrawablesToCurrentState()
            eRecipeNote.setText(viewModel.recipeNote)
            refreshPart()

            eRecipeTitle.addTextChangedListener {
                viewModel.recipeTitle = it.toString()
            }

            cbFavorite.setOnCheckedChangeListener { _, isChecked ->
                viewModel.recipeFavorite = isChecked
            }
            eRecipeNote.addTextChangedListener {
                viewModel.recipeNote = it.toString()
            }

            eRecipeDescription.addTextChangedListener {
                viewModel.recipePart = it.toString()
            }

            bNextPart.setOnClickListener {
                viewModel.onNextClick()
            }

            bPreviousPart.setOnClickListener {
                viewModel.onPreciousClick()
            }

            bDeletePart.setOnClickListener {
                viewModel.onDeleteClick()
            }

            saveRecipe.setOnClickListener {
                eRecipeTitle.clearFocus()
                eRecipeNote.clearFocus()
                eRecipeDescription.clearFocus()
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditRecipeEvent.collect { event ->
                when (event) {
                    is RecipeAddEditEvent.NavigateBackWithResult -> {
                        binding.eRecipeTitle.clearFocus()
                        binding.eRecipeNote.clearFocus()
                        binding.eRecipeDescription.clearFocus()
                        setFragmentResult(
                            "add_edit_request", bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is RecipeAddEditEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is RecipeAddEditEvent.ShowNewPart -> {
                        binding.bPreviousPart.isClickable = viewModel.currentPart > 0
                        refreshPart()
                    }
                }.exhaustive
            }
        }
    }

}