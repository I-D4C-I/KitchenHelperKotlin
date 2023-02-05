package com.example.kitchenhelperkotlin.products.addedit

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentAddEditProductsBinding
import com.example.kitchenhelperkotlin.events.AddEditEvent
import com.example.kitchenhelperkotlin.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject


@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class AddEditProductFragment : Fragment(R.layout.fragment_add_edit_products) {

    @Inject
    lateinit var factory: AddEditProductViewModel.AddEditFactory

    private val viewModel: AddEditProductViewModel by viewModels {
        AddEditProductViewModel.provideFactory(factory, this, arguments)
    }

    private var date = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditProductsBinding.bind(view)

        fun updateDate() {
            binding.bExpirationDate.setText(
                DateUtils.formatDateTime(
                    context,
                    date.timeInMillis,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                )
            )
        }

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            date.set(year, month, dayOfMonth)
            updateDate()
        }

        binding.apply {
            eProductTitle.setText(viewModel.productTitle)

            if (viewModel.product != null) {
                eProductAmount.setText(viewModel.productAmount)

                date.set(
                    viewModel.productDate!!.year,
                    viewModel.productDate!!.monthValue - 1,
                    viewModel.productDate!!.dayOfMonth,
                )
            } else {
                eProductAmount.setText("0")
            }

            updateDate()

            bExpirationDate.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    datePicker,
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            eProductTitle.addTextChangedListener {
                viewModel.productTitle = it.toString()
            }
            eProductAmount.addTextChangedListener {
                viewModel.productAmount = it.toString()
            }
            bExpirationDate.addTextChangedListener {
                viewModel.productDate =
                    LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate()
            }

            bIncreaseAmount.setOnClickListener {
                val amount = eProductAmount.text.toString().toInt() + 1
                eProductAmount.setText(amount.toString())
            }

            bDecreaseAmount.setOnClickListener {
                var amount = eProductAmount.text.toString().toInt() - 1
                if(amount <= 0)
                    amount = 0
                eProductAmount.setText(amount.toString())
            }

            saveProduct.setOnClickListener {
                eProductTitle.clearFocus()
                eProductAmount.clearFocus()
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditProductEvent.collect { event ->
                when (event) {
                    is AddEditEvent.NavigateBackWithResult -> {
                        binding.eProductTitle.clearFocus()
                        binding.eProductAmount.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
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