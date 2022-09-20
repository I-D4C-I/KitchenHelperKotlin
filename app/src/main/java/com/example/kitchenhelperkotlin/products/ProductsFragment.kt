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

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProductsBinding.bind(view)


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_product_menu, menu)

                val searchItem = menu.findItem(R.id.actionSearch)
                searchView = searchItem.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }

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
                //TODO: Необходим нормальный drag and drop, ItemTouchHelper.UP or ItemTouchHelper.DOWN
                var fromPosition = -1
                var toPosition = -1

                override fun onMove(
                    recyclerView: RecyclerView,
                    source: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    toPosition = target.adapterPosition
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val product = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.onProductSwiped(product)
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
                        viewHolder?.itemView?.alpha = 0.5f

                    when (actionState) {
                        ItemTouchHelper.ACTION_STATE_DRAG -> {
                            fromPosition = viewHolder?.adapterPosition!!
                        }
                        ItemTouchHelper.ACTION_STATE_IDLE -> {
                            if (fromPosition != -1 && toPosition != -1 && fromPosition != toPosition) {
                                /*
                                val productFrom = adapter.currentList[fromPosition]
                                val productTo = adapter.currentList[toPosition]

                                viewModel.onProductMoved(productFrom,productTo)

                                toPosition = -1
                                fromPosition = -1

                                 */
                            }
                        }
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1f
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
                        val action =
                            ProductsFragmentDirections.actionProductsFragmentToAddEditProductFragment(
                                resources.getString(R.string.addNew), null
                            )
                        findNavController().navigate(action)
                    }
                    is ProductEvent.NavigateToEditProductScreen -> {
                        val action =
                            ProductsFragmentDirections.actionProductsFragmentToAddEditProductFragment(
                                resources.getString(R.string.edit),
                                event.product
                            )
                        findNavController().navigate(action)
                    }
                    is ProductEvent.ShowSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is ProductEvent.NavigateToAddToBuyScreen -> {
                        val action =
                            ProductsFragmentDirections.actionProductsFragmentToAddEditToBuyFragment(
                                resources.getString(R.string.addNew),
                                event.productTitle
                            )
                        findNavController().navigate(action)
                    }
                }
            }.exhaustive
        }
    }

    override fun onItemClick(product: Product) {
        viewModel.onProductSelected(product)
    }

    override fun onAddToBuyListClick(productTitle: String) {
        viewModel.onProductToBuyListClick(productTitle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}