package com.example.kitchenhelperkotlin.tobuy.addedit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentAddEditTobuyBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddEditToBuyFragment : Fragment(R.layout.fragment_add_edit_tobuy) {

    private val args : AddEditToBuyFragmentArgs by navArgs()

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
            eToBuyAmount.setText(viewModel.toBuyAmount.toString())
            cpImportant.isChecked = viewModel.toBuyImportance
            cpImportant.jumpDrawablesToCurrentState()
        }
    }
}