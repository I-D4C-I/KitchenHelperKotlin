package com.example.kitchenhelperkotlin.tobuy

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
import com.example.kitchenhelperkotlin.databinding.FragmentTobuyBinding
import com.example.kitchenhelperkotlin.events.ToBuyEvent
import com.example.kitchenhelperkotlin.notifications.NotificationBottomSheet
import com.example.kitchenhelperkotlin.util.ADD_EDIT_REQUEST
import com.example.kitchenhelperkotlin.util.ADD_EDIT_RESULT
import com.example.kitchenhelperkotlin.util.exhaustive
import com.example.kitchenhelperkotlin.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ToBuyFragment : Fragment(R.layout.fragment_tobuy), ToBuyAdapter.OnItemClickListener {


    @Inject
    lateinit var factory: ToBuyViewModel.ToBuyModelFactory

    private val viewModel: ToBuyViewModel by viewModels {
        ToBuyViewModel.provideFactory(factory, this, arguments)
    }

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTobuyBinding.bind(view)

        //setHasOptionsMenu(true)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_tobuy_menu, menu)

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

                viewLifecycleOwner.lifecycleScope.launch {
                    menu.findItem(R.id.hideCompleted).isChecked =
                        viewModel.preferencesFlow.first().hideCompleted
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.sortBuyName -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                        true
                    }
                    R.id.sortBuyDate -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                        true
                    }
                    R.id.hideCompleted -> {
                        menuItem.isChecked = !menuItem.isChecked
                        viewModel.onCompletedClick(menuItem.isChecked)
                        true
                    }
                    R.id.deleteCompleted -> {
                        viewModel.onDeleteAllCompletedClick()
                        true
                    }
                    R.id.actionNotification -> {
                        viewModel.onCreateNotificationClick()
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

        setFragmentResultListener(ADD_EDIT_REQUEST) { _, bundle ->
            val result = bundle.getInt(ADD_EDIT_RESULT)
            viewModel.onAddEditResult(result)
        }

        viewModel.toBuys.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.toBuyEvent.collect { event ->
                when (event) {
                    is ToBuyEvent.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), R.string.deletedToBuy, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) {
                                viewModel.onUndoDeleteClick(event.toBuy)
                            }.show()
                    }
                    is ToBuyEvent.NavigateToAddScreen -> {
                        val action =
                            ToBuyFragmentDirections.actionToBuyFragmentToAddEditToBuyFragment(
                                resources.getString(R.string.addNew),
                                null
                            )
                        findNavController().navigate(action)
                    }
                    is ToBuyEvent.NavigateToEditScreen -> {
                        val action =
                            ToBuyFragmentDirections.actionToBuyFragmentToAddEditToBuyFragment(
                                resources.getString(R.string.edit),
                                event.toBuy
                            )
                        findNavController().navigate(action)
                    }
                    is ToBuyEvent.ShowConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is ToBuyEvent.NavigateToDeleteAllScreen -> {
                        val action =
                            ToBuyFragmentDirections.actionGlobalDeleteAllDialogFragment()
                        findNavController().navigate(action)
                    }
                    is ToBuyEvent.ShowCreateNotificationBottomSheet -> {
                        NotificationBottomSheet(viewModel).show(
                            requireActivity().supportFragmentManager,
                            "BottomSheet"
                        )
                    }
                    is ToBuyEvent.ShowCreateNotificationMessage -> {
                        Snackbar.make(
                            requireView(),
                            R.string.notificationCreated,
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(R.string.undo) {
                                viewModel.onCancelNotificationClick(event.tag)
                            }.show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onItemClick(toBuy: ToBuy) {
        viewModel.onToBuySelected(toBuy)
    }

    override fun onCheckBoxClick(toBuy: ToBuy, isChecked: Boolean) {
        viewModel.onToBuyCheckedChanged(toBuy, isChecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}