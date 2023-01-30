package com.example.kitchenhelperkotlin.events

sealed class AddEditEvent{
    data class ShowInvalidInputMessage(val msg: String) : AddEditEvent()
    data class NavigateBackWithResult(val result: Int) : AddEditEvent()
}
