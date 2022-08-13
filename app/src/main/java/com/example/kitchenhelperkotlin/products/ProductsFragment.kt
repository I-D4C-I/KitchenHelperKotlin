package com.example.kitchenhelperkotlin.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentProductsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val viewModel : ProductViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProductsBinding.bind(view)
    }
}