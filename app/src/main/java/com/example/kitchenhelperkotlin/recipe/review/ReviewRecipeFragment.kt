package com.example.kitchenhelperkotlin.recipe.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentReviewRecipeBinding
import com.example.kitchenhelperkotlin.events.recipeEvents.RecipeReviewEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReviewRecipeFragment : Fragment(R.layout.fragment_review_recipe) {

    @Inject
    lateinit var factory: ReviewRecipeViewModel.ViewFactory

    private val viewModel: ReviewRecipeViewModel by viewModels {
        ReviewRecipeViewModel.provideFactory(factory, this, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentReviewRecipeBinding.bind(view)

        binding.apply {
            fbEditRecipe.setOnClickListener {
                viewModel.onEditClick()
            }
        }

        viewModel.obsRecipe.observe(viewLifecycleOwner) { recipe ->
            binding.testButton.text = recipe.title
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onEditResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewRecipeEvent.collect{event ->
                when(event){
                    is RecipeReviewEvent.NavigateToEditRecipeScreen -> {
                        val action = ReviewRecipeFragmentDirections.actionReviewRecipeFragmentToAddEditRecipeFragment(resources.getString(R.string.edit), viewModel.obsRecipe.value)
                        findNavController().navigate(action)
                    }
                    is RecipeReviewEvent.ShowSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.text, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}