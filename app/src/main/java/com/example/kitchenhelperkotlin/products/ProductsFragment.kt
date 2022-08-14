package com.example.kitchenhelperkotlin.products

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.databinding.FragmentProductsBinding
import com.example.kitchenhelperkotlin.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val viewModel: ProductViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProductsBinding.bind(view)


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_product_menu, menu)

                val searchItem = menu.findItem(R.id.actionSearch)
                val searchView = searchItem.actionView as SearchView

                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.actionSortDefault -> {
                        viewModel.sortOrder.value = SortOrder.DEFAULT
                        true
                    }
                    R.id.actionSortByName -> {
                        viewModel.sortOrder.value = SortOrder.BY_NAME
                        true
                    }
                    R.id.actionSortByDate -> {
                        viewModel.sortOrder.value = SortOrder.BY_DATE
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        val adapter = ProductAdapter()

        binding.apply {
            recycleViewProduct.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.products.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}