package com.example.kitchenhelperkotlin.tobuy

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.databinding.FragmentTobuyBinding
import com.example.kitchenhelperkotlin.util.ItemEvent
import com.example.kitchenhelperkotlin.util.OnItemClickListener
import com.example.kitchenhelperkotlin.util.OnQueryTextChanged
import com.example.kitchenhelperkotlin.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO("Заменить setHasOptionsMenu")
@AndroidEntryPoint
class ToBuyFragment : Fragment(R.layout.fragment_tobuy), OnItemClickListener {


    @Inject
    lateinit var factory: ToBuyViewModel.ToBuyModelFactory

    private val viewModel: ToBuyViewModel by viewModels {
        ToBuyViewModel.provideFactory(factory, this, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTobuyBinding.bind(view)
        setHasOptionsMenu(true)


        val adapter = ToBuyAdapter(this)
        binding.apply {
            recycleViewToBuy.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val toBuy = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.onToBuySwiped(toBuy)
                }

            }).attachToRecyclerView(recycleViewToBuy)

            bAddToBuy.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }

        setFragmentResultListener("add_edit_request") {_, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.toBuys.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.toBuyEvent.collect { event ->
                when (event) {
                    is ItemEvent.ShowUndoDeleteToBuyMessage -> {
                        Snackbar.make(requireView(), R.string.deletedToBuy, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) {
                                viewModel.onUndoDeleteClick(event.toBuy)
                            }.show()
                    }
                    is ItemEvent.NavigateToAddScreen -> {
                        val action =
                            ToBuyFragmentDirections.actionToBuyFragmentToAddEditToBuyFragment(resources.getString(R.string.addNew),null)
                        findNavController().navigate(action)
                    }
                    is ItemEvent.NavigateToEditToBuyScreen -> {
                        val action =
                            ToBuyFragmentDirections.actionToBuyFragmentToAddEditToBuyFragment(resources.getString(R.string.edit) ,event.toBuy)
                        findNavController().navigate(action)
                    }
                    is ItemEvent.ShowConfirmationMessage -> {
                        Snackbar.make(requireView(),event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_tobuy_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.OnQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.hideCompleted).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sortBuyName -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.sortBuyDate -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.hideCompleted -> {
                item.isChecked = !item.isChecked
                viewModel.onCompletedClick(item.isChecked)
                true
            }
            R.id.deleteComleted -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(toBuy: ToBuy) {
        viewModel.onToBuySelected(toBuy)
    }

    override fun onCheckBoxClick(toBuy: ToBuy, isChecked: Boolean) {
        viewModel.onToBuyCheckedChanged(toBuy, isChecked)
    }
}