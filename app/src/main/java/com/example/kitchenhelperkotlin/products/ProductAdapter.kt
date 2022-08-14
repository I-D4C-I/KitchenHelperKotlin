package com.example.kitchenhelperkotlin.products

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.ProductItemBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = getItem(position)
        holder.bind(currentProduct)
    }

    class ProductViewHolder(
        private val binding: ProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                productTitle.text = product.title
                productAmount.text = product.amount.toString()

                val between = ChronoUnit.DAYS.between(LocalDate.now(), product.date).toInt()
                if (between < 7)
                    productLeftDays.setTextColor(Color.RED)
                if (between <= 0)
                    productLeftDays.text = itemView.context.getString(R.string.expired)
                else
                    productLeftDays.text = between.toString()

            }
        }
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