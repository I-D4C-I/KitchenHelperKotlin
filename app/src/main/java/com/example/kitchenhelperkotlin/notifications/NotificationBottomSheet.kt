package com.example.kitchenhelperkotlin.notifications

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.CreateNotificationBottomSheetBinding
import com.example.kitchenhelperkotlin.events.NotificationBottomSheetEvent
import com.example.kitchenhelperkotlin.tobuy.ToBuyViewModel
import com.example.kitchenhelperkotlin.util.exhaustive
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

//TODO: Из-за добавления toBuyViewModel как параметра, при повороте экрана приложение вылетает. Нужно как-то передать информацию другим путем

@AndroidEntryPoint
class NotificationBottomSheet(val toBuyViewModel: ToBuyViewModel) :
    BottomSheetDialogFragment() {

    private val viewModel: NotificationBottomSheetViewModel by viewModels()

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


        binding.apply {
            viewModel.callUpdateTime()
            eSelectTime.setOnClickListener {
                viewModel.onSelectTimeClick()
            }
            cbShowOnNextDay.setOnCheckedChangeListener { _, isChecked ->
                viewModel.showOnNextDay = isChecked
            }
            bAddNotification.setOnClickListener {
                viewModel.addNotificationClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bottomSheetEvent.collect { event ->
                when (event) {
                    is NotificationBottomSheetEvent.UpdateTime -> {
                        binding.eSelectTime.setText(
                            viewModel.formatter.format(viewModel.calendar.time)
                        )
                    }
                    is NotificationBottomSheetEvent.ShowTimePickerDialog -> {
                        TimePickerDialog(
                            requireContext(),
                            viewModel.timePiker,
                            viewModel.calendar.get(Calendar.HOUR_OF_DAY),
                            viewModel.calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }
                    is NotificationBottomSheetEvent.ShowInvalidInputMessage -> {
                        Toast.makeText(requireContext(), R.string.retype, Toast.LENGTH_LONG).show()
                    }
                    is NotificationBottomSheetEvent.NavigateBackWithResult -> {
                        toBuyViewModel.createNotification(event.diff)
                        dismiss()
                    }

                }.exhaustive
            }
        }
    }
}