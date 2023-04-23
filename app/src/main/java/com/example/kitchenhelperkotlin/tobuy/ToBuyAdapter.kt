package com.example.kitchenhelperkotlin.tobuy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenhelperkotlin.databinding.TobuyItemBinding

class ToBuyAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<ToBuy, ToBuyAdapter.ToBuyViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToBuyViewHolder {
        val binding = TobuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToBuyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ToBuyViewHolder(
        private val binding: TobuyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val toBuy = getItem(position)
                        listener.onItemClick(toBuy)
                    }
                }
                cbCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val toBuy = getItem(position)
                        listener.onCheckBoxClick(toBuy, cbCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(toBuy: ToBuy) {
            binding.apply {
                cbCompleted.isChecked = toBuy.completed
                tobuyName.text = toBuy.title
                tobuyName.paint.isStrikeThruText = toBuy.completed
                tobuyAmount.text = toBuy.amount.toString()
                if (toBuy.amount == 0)
                    tobuyAmount.isVisible = false
                tobuyPriority.isVisible = toBuy.important
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(toBuy: ToBuy)
        fun onCheckBoxClick(toBuy: ToBuy, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<ToBuy>() {
        override fun areItemsTheSame(oldItem: ToBuy, newItem: ToBuy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToBuy, newItem: ToBuy): Boolean {
            return oldItem == newItem
        }
    }
}