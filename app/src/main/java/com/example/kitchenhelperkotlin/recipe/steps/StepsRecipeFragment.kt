package com.example.kitchenhelperkotlin.recipe.steps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentStepsRecipeBinding

class StepsRecipeFragment : Fragment(R.layout.fragment_steps_recipe) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentStepsRecipeBinding.bind(view)

    }
}