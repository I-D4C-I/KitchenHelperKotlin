package com.example.kitchenhelperkotlin.recipe

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kitchenhelperkotlin.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecipeFragment : Fragment(R.layout.fragment_recipe) {

    private val viewModel : RecipeViewModel by viewModels()
}