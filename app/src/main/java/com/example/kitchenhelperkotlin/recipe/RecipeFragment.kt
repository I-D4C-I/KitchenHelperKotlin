package com.example.kitchenhelperkotlin.recipe

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
import com.example.kitchenhelperkotlin.databinding.FragmentRecipeBinding
import com.example.kitchenhelperkotlin.events.RecipeEvent
import com.example.kitchenhelperkotlin.util.exhaustive
import com.example.kitchenhelperkotlin.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecipeFragment : Fragment(R.layout.fragment_recipe), RecipeAdapter.OnItemClickListener {

    @Inject
    lateinit var factory: RecipeViewModel.ProductModelFactory

    private val viewModel: RecipeViewModel by viewModels {
        RecipeViewModel.provideFactory(factory, this, arguments)
    }

    private lateinit var searchView : SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRecipeBinding.bind(view)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_recipe_menu, menu)

                val searchItem = menu.findItem(R.id.actionSearch)
                searchView = searchItem.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery!= null && pendingQuery.isNotEmpty()){
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery,false)
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
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val recipeAdapter = RecipeAdapter(this)
        binding.apply {
            recycleViewRecipe.apply {
                adapter = recipeAdapter
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
                    val recipe = recipeAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onRecipeSwiped(recipe)
                }
            }).attachToRecyclerView(recycleViewRecipe)

            fabAddRecipe.setOnClickListener {
                viewModel.onAddNewRecipeClick()
            }
        }

        setFragmentResultListener("add_edit_request"){ _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.recipes.observe(viewLifecycleOwner) {
            recipeAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recipeEvent.collect { event ->
                when (event) {
                    is RecipeEvent.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), R.string.recipeDeleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.cancel) {
                                viewModel.onUndoDeleteClick(event.recipe)
                            }.show()
                    }
                    is RecipeEvent.NavigateToAddRecipeScreen -> {
                        val action = RecipeFragmentDirections.actionRecipeFragmentToAddEditRecipeFragment(resources.getString(R.string.addNew), null)
                        findNavController().navigate(action)
                    }
                    is RecipeEvent.NavigateToEditRecipeScreen -> {
                        val action = RecipeFragmentDirections.actionRecipeFragmentToAddEditRecipeFragment(resources.getString(R.string.edit), event.recipe )
                        findNavController().navigate(action)
                    }
                    is RecipeEvent.ShowSavedConfirmationMessage -> {
                      Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

    override fun onItemClick(recipe: Recipe) {
        viewModel.onRecipeSelected(recipe)
    }
}