package com.example.kitchenhelperkotlin.events

import com.example.kitchenhelperkotlin.products.Product

sealed class ProductEvent {
    object NavigateToAddProductScreen : ProductEvent()
    data class NavigateToEditProductScreen(val product: Product) : ProductEvent()
    data class ShowUndoDeleteMessage(val product: Product) : ProductEvent()

}
