package com.example.kitchenhelperkotlin.util

import com.example.kitchenhelperkotlin.tobuy.ToBuy

sealed class ItemEvent{
    data class ShowUndoDeleteToBuyMessage(val toBuy: ToBuy) :ItemEvent()
}
