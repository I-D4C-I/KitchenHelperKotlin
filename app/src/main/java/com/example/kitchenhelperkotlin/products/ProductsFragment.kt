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
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.databinding.FragmentProductsBinding
import com.example.kitchenhelperkotlin.events.ProductEvent
import com.example.kitchenhelperkotlin.util.exhaustive
import com.example.kitchenhelperkotlin.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products), ProductAdapter.OnItemClickListener {


    @Inject
    lateinit var factory: ProductViewModel.ProductModelFactory

    private val viewModel: ProductViewModel by viewModels {
        ProductViewModel.provideFactory(factory, this, arguments)
    }


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
                        viewModel.onSortOrderSelected(SortOrder.DEFAULT)
                        true
                    }
                    R.id.actionSortByName -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                        true
                    }
                    R.id.actionSortByDate -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        val adapter = ProductAdapter(this)

        binding.apply {
            recycleViewProduct.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    source: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val product = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.onProductSwiped(product)
                }
            }).attachToRecyclerView(recycleViewProduct)

            fabAddProduct.setOnClickListener {
                viewModel.addNewProductClick()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.products.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.productEvent.collect { event ->
                when (event) {
                    is ProductEvent.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), R.string.productDeleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) {
                                viewModel.onUndoDeleteClick(event.product)
                            }.show()
                    }
                    is ProductEvent.NavigateToAddProductScreen -> {
                        val acton =
                            ProductsFragmentDirections.actionProductsFragmentToAddEditProductFragment(
                                resources.getString(R.string.addNew), null
                            )
                        findNavController().navigate(acton)
                    }
                    is ProductEvent.NavigateToEditProductScreen -> {
                        val acton =
                            ProductsFragmentDirections.actionProductsFragmentToAddEditProductFragment(
                                resources.getString(R.string.edit),
                                event.product
                            )
                        findNavController().navigate(acton)
                    }
                    is ProductEvent.ShowSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }.exhaustive
        }
    }

    override fun onItemClick(product: Product) {
        viewModel.onProductSelected(product)
    }
}