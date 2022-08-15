package com.example.kitchenhelperkotlin.events

import com.example.kitchenhelperkotlin.products.Product

sealed class ProductEvent{
    data class ShowUndoDeleteMessage(val product: Product) : ProductEvent()
}
