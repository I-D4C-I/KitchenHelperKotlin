package com.example.kitchenhelperkotlin.notifications

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.CreateNotificationBottomSheetBinding
import com.example.kitchenhelperkotlin.tobuy.ToBuyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class NotificationBottomSheet(private val viewModel: ToBuyViewModel) : BottomSheetDialogFragment() {

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
    private val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_notification_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = CreateNotificationBottomSheetBinding.bind(view)

        fun updateDate() {
            binding.eSelectTime.setText(
                formatter.format(calendar.time)
            )
        }

        val timePiker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay,
                minute,
                0
            )
            updateDate()
        }

        binding.apply {
            updateDate()

            eSelectTime.setOnClickListener {
                TimePickerDialog(
                    requireContext(),
                    timePiker,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }

            bAddNotification.setOnClickListener {
                //Деление на 1000 превращает время в милисекундах в секунды
                //Вычисляем разницу между целевым временем и текущим в секундах
                val today = Calendar.getInstance(TimeZone.getDefault())
                val diff = (calendar.timeInMillis/1000L) - (today.timeInMillis/1000L)
                viewModel.createNotification(requireContext(), diff)
                dismiss()
            }

        }
    }

}