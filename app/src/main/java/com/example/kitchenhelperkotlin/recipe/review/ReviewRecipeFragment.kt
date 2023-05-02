package com.example.kitchenhelperkotlin.recipe.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentReviewRecipeBinding
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

        }


        viewModel.obsRecipe.observe(viewLifecycleOwner) { recipe ->
            binding.testButton.text = recipe.title
        }
    }
}