package com.example.kitchenhelperkotlin.notifications

import android.app.TimePickerDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitchenhelperkotlin.events.NotificationBottomSheetEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotificationBottomSheetViewModel @Inject constructor() :
    ViewModel() {

    val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    var showOnNextDay = false

    private val bottomSheetEventChannel = Channel<NotificationBottomSheetEvent>()
    val bottomSheetEvent = bottomSheetEventChannel.receiveAsFlow()

    val timePiker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            hourOfDay,
            minute,
            0
        )
        callUpdateTime()
    }

    fun addNotificationClick() {
        //Деление на 1000 превращает время в милисекундах в секунды
        //Вычисляем разницу между целевым временем и текущим в секундах
        val today = Calendar.getInstance(TimeZone.getDefault())
        if (calendar.get(Calendar.HOUR_OF_DAY) < today.get(Calendar.HOUR_OF_DAY) && !showOnNextDay) {
            showInvalidInputMessage()
            return
        } else {
            if (showOnNextDay) {
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH) + 1
                )
            }
            val diff = (calendar.timeInMillis / 1000L) - (today.timeInMillis / 1000L)
            createNotification(diff)
        }
    }

    private fun createNotification(diff: Long) = viewModelScope.launch {
        bottomSheetEventChannel.send(NotificationBottomSheetEvent.NavigateBackWithResult(diff))
    }

    fun callUpdateTime() = viewModelScope.launch {
        bottomSheetEventChannel.send(NotificationBottomSheetEvent.UpdateTime)
    }

    fun onSelectTimeClick() = viewModelScope.launch {
        bottomSheetEventChannel.send(NotificationBottomSheetEvent.ShowTimePickerDialog)
    }

    private fun showInvalidInputMessage() = viewModelScope.launch {
        bottomSheetEventChannel.send(NotificationBottomSheetEvent.ShowInvalidInputMessage)
    }
}