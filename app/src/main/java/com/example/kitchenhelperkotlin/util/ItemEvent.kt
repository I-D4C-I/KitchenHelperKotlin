package com.example.kitchenhelperkotlin.util

import com.example.kitchenhelperkotlin.tobuy.ToBuy

sealed class ItemEvent {
    object NavigateToAddScreen : ItemEvent()
    data class NavigateToEditToBuyScreen(val toBuy: ToBuy) : ItemEvent()
    data class ShowUndoDeleteToBuyMessage(val toBuy: ToBuy) : ItemEvent()
    data class ShowConfirmationMessage(val msg: String) : ItemEvent()
    object NavigateToDeleteAllScreen : ItemEvent()
}
