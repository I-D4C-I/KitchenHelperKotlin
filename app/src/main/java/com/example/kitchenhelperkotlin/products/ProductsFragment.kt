package com.example.kitchenhelperkotlin.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.*


class ProductsFragment : Fragment(R.layout.fragment_products) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProductsBinding.bind(view)

        binding.apply {
            testButton.setOnClickListener {

            }
        }
    }

}