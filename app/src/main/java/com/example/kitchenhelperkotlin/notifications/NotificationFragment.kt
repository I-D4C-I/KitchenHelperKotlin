package com.example.kitchenhelperkotlin.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentNotificationBinding
import com.example.kitchenhelperkotlin.util.NotificationHelper

class NotificationFragment : Fragment(R.layout.fragment_notification) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNotificationBinding.bind(view)

        binding.apply {
            notify.setOnClickListener {
                NotificationHelper(requireContext())
                    .sendNotification(description = "Не забудьте купить продукты!")
            }
        }
    }
}