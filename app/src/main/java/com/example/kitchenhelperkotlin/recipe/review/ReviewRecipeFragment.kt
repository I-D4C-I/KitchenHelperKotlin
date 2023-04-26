package com.example.kitchenhelperkotlin.recipe.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentReviewRecipeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReviewRecipeFragment : Fragment(R.layout.fragment_review_recipe) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentReviewRecipeBinding.bind(view)

        binding.apply {

        }


    }
}