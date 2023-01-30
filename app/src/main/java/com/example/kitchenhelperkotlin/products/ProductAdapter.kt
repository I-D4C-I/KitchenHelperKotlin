package com.example.kitchenhelperkotlin.products

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.ProductItemBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class ProductAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = getItem(position)
        holder.bind(currentProduct)
    }

    inner class ProductViewHolder(
        private val binding: ProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val product = getItem(position)
                        listener.onItemClick(product)
                    }
                }

            }
        }

        fun bind(product: Product) {
            binding.apply {
                productTitle.text = product.title
                productAmount.text = product.amount.toString()

                val between = ChronoUnit.DAYS.between(LocalDate.now(), product.date).toInt()
                productLeftDays.text = between.toString()
                if (between < 7)
                    productLeftDays.setTextColor(Color.RED)
                if (between <= 0)
                    productLeftDays.text = itemView.context.getString(R.string.expired)

                bShowAddToBuy.setOnClickListener {
                    listener.onAddToBuyListClick(product.title)
                }


                root.animation = AnimationUtils.loadAnimation(
                    binding.root.context,
                    R.anim.item_animation_from_bottom
                )
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(product: Product)
        fun onAddToBuyListClick(productTitle: String)
    }

    class DiffCallBack : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
    }

}