package com.example.kitchenhelperkotlin.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.CreateNotificationBottomSheetBinding
import com.example.kitchenhelperkotlin.tobuy.ToBuyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationBottomSheet(private val viewModel: ToBuyViewModel) : BottomSheetDialogFragment() {

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
            bAddNotification.setOnClickListener {
                //viewModel.createNotification()
                dismiss()
            }
        }
    }

}