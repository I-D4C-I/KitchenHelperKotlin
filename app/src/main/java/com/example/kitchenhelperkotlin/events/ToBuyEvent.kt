package com.example.kitchenhelperkotlin.events

import com.example.kitchenhelperkotlin.tobuy.ToBuy

sealed class ToBuyEvent {
    object NavigateToAddScreen : ToBuyEvent()
    data class NavigateToEditScreen(val toBuy: ToBuy) : ToBuyEvent()
    object ShowCreateNotificationBottomSheet : ToBuyEvent()
    data class ShowUndoDeleteMessage(val toBuy: ToBuy) : ToBuyEvent()
    data class ShowConfirmationMessage(val msg: String) : ToBuyEvent()
    class ShowCreateNotificationMessage(val tag: String) : ToBuyEvent()
    object NavigateToDeleteAllScreen : ToBuyEvent()
}
