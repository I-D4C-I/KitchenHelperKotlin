package com.example.kitchenhelperkotlin.tobuy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenhelperkotlin.databinding.TobuyItemBinding

class ToBuyAdapter : ListAdapter<ToBuy, ToBuyAdapter.ToBuyViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToBuyViewHolder {
        val binding = TobuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToBuyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ToBuyViewHolder(
        private val binding: TobuyItemBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(toBuy: ToBuy){
            binding.apply {
                cbCompleted.isChecked = toBuy.completed
                tobuyName.text = toBuy.title
                tobuyName.paint.isStrikeThruText = toBuy.completed
                tobuyAmount.text = toBuy.amount.toString()
                tobuyPriority.isVisible = toBuy.important
            }
        }
    }

    class DiffCallback :DiffUtil.ItemCallback<ToBuy>() {
        override fun areItemsTheSame(oldItem: ToBuy, newItem: ToBuy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToBuy, newItem: ToBuy): Boolean {
            return oldItem == newItem
        }
    }
}