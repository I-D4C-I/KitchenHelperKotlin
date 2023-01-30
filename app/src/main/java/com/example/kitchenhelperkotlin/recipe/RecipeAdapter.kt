package com.example.kitchenhelperkotlin.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.RecipeItemBinding

class RecipeAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class RecipeViewHolder(private val binding: RecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val recipe = getItem(position)
                        listener.onItemClick(recipe)
                    }
                }
            }
        }

        fun bind(recipe: Recipe) {
            binding.apply {
                recipeTitle.text = recipe.title
                recipeFavorite.isVisible = recipe.favorite
                recipeNote.text = recipe.note

                root.animation = AnimationUtils.loadAnimation(
                    binding.root.context,
                    R.anim.item_animation_from_bottom
                )
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    class DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem == newItem
    }
}