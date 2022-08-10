package com.example.kitchenhelperkotlin.tobuy

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.SortOrder
import com.example.kitchenhelperkotlin.databinding.FragmentTobuyBinding
import com.example.kitchenhelperkotlin.util.OnQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

//TODO("Заменить setHasOptionsMenu")
@AndroidEntryPoint
class ToBuyFragment : Fragment(R.layout.fragment_tobuy) {

    private val  viewModel : ToBuyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTobuyBinding.bind(view)
        setHasOptionsMenu(true)


        val adapter = ToBuyAdapter()
        binding.apply {
            recycleViewToBuy.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.toBuys.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_tobuy_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.OnQueryTextChanged{
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sortBuyName -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }
            R.id.sortBuyDate-> {
                viewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }
            R.id.hideCompleted-> {
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }
            R.id.deleteComleted-> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}