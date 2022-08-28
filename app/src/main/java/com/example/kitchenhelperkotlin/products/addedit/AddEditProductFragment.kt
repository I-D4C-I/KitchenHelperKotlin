package com.example.kitchenhelperkotlin.products.addedit

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
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
            bExpirationDate.text = DateUtils.formatDateTime(
                context,
                date.timeInMillis,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )

        }
    }

}