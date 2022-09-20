package com.example.kitchenhelperkotlin.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentSettingsBinding
import com.example.kitchenhelperkotlin.util.DELETE_ALL_PRODUCTS
import com.example.kitchenhelperkotlin.util.DELETE_ALL_TOBUY
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSettingsBinding.bind(view)

        binding.apply {
            bClearAllProducts.setOnClickListener {
                val action =
                    SettingsFragmentDirections.actionGlobalDeleteAllDialogFragment(DELETE_ALL_PRODUCTS)
                findNavController().navigate(action)
            }
            bClearAllToBuy.setOnClickListener {
                val action =
                    SettingsFragmentDirections.actionGlobalDeleteAllDialogFragment(DELETE_ALL_TOBUY)
                findNavController().navigate(action)
            }
        }
    }

}