package com.example.kitchenhelperkotlin.recipe.steps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentStepsRecipeBinding
import javax.inject.Inject

class StepsRecipeFragment : Fragment(R.layout.fragment_steps_recipe) {

    @Inject
    lateinit var factory: StepsRecipeViewModel.ViewFactory

    private val viewModel: StepsRecipeViewModel by viewModels {
        StepsRecipeViewModel.provideFactory(factory, this, arguments)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentStepsRecipeBinding.bind(view)

        fun refreshPart() {
            binding.eRecipeStep.clearFocus()
            binding.vPart.text = getString(R.string.part, viewModel.numberOfStep + 1)
            binding.eRecipeStep.setText(viewModel.step)
        }

        binding.apply {
            refreshPart()

        }
    }
}