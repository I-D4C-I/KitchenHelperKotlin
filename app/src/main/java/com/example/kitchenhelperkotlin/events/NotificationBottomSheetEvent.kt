package com.example.kitchenhelperkotlin.events

sealed class NotificationBottomSheetEvent {
    object UpdateTime : NotificationBottomSheetEvent()
    object ShowTimePickerDialog : NotificationBottomSheetEvent()
    object ShowInvalidInputMessage : NotificationBottomSheetEvent()
    data class NavigateBackWithResult(val diff: Long) : NotificationBottomSheetEvent()
}