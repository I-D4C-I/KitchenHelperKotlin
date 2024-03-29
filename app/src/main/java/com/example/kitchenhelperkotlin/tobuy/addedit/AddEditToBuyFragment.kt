package com.example.kitchenhelperkotlin.tobuy.addedit

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
import com.example.kitchenhelperkotlin.databinding.FragmentAddEditTobuyBinding
import com.example.kitchenhelperkotlin.events.AddEditEvent
import com.example.kitchenhelperkotlin.util.ADD_EDIT_REQUEST
import com.example.kitchenhelperkotlin.util.ADD_EDIT_RESULT
import com.example.kitchenhelperkotlin.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class AddEditToBuyFragment : Fragment(R.layout.fragment_add_edit_tobuy) {

    @Inject
    lateinit var factory: AddEditToBuyViewModel.AddEditFactory

    private val viewModel: AddEditToBuyViewModel by viewModels {

        AddEditToBuyViewModel.provideFactory(factory, this, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTobuyBinding.bind(view)

        binding.apply {
            eToBuyTitle.setText(viewModel.toBuyTitle)
            eToBuyAmount.setText(viewModel.toBuyAmount)
            if (viewModel.toBuyAmount == "null")
                eToBuyAmount.setText("")

            if (viewModel.productToBuy != null)
                eToBuyTitle.setText(viewModel.productToBuy)

            cbImportant.isChecked = viewModel.toBuyImportance
            cbImportant.jumpDrawablesToCurrentState()


            eToBuyTitle.addTextChangedListener {
                viewModel.toBuyTitle = it.toString()
            }
            eToBuyAmount.addTextChangedListener {
                viewModel.toBuyAmount = it.toString()
            }
            cbImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.toBuyImportance = isChecked
            }
            saveToBuy.setOnClickListener {
                eToBuyTitle.clearFocus()
                eToBuyAmount.clearFocus()
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditToBuyEvent.collect { event ->
                when (event) {
                    is AddEditEvent.NavigateBackWithResult -> {
                        binding.eToBuyTitle.clearFocus()
                        binding.eToBuyAmount.clearFocus()
                        setFragmentResult(
                            ADD_EDIT_REQUEST,
                            bundleOf(ADD_EDIT_RESULT to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }
}