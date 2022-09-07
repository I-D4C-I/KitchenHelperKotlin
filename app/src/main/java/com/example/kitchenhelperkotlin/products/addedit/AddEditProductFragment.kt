package com.example.kitchenhelperkotlin.products.addedit

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentAddEditProductsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


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

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            date.set(year,month,dayOfMonth)
            updateDate()
        }

        val binding = FragmentAddEditProductsBinding.bind(view)

        binding.apply {
            eProductTitle.setText(viewModel.productTitle)

            if (viewModel.product != null)
                eProductAmount.setText(viewModel.productAmount)

            if (viewModel.product != null) {
                date.set(
                    viewModel.productDate!!.year,
                    viewModel.productDate!!.monthValue - 1,
                    viewModel.productDate!!.dayOfMonth,
                )
            }
            updateDate()
            bExpirationDate.setOnClickListener {
                context?.let { it1 -> DatePickerDialog(it1,datePicker,date.get(Calendar.YEAR),date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                    .show()}
            }

        }
    }
    private fun updateDate() {
        val bExpirationDate = view?.findViewById<Button>(R.id.bExpirationDate)
        bExpirationDate?.text = DateUtils.formatDateTime(
            context,
            date.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
        )
    }
}