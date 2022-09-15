package com.example.kitchenhelperkotlin.notifications

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitchenhelperkotlin.KHApplication.Companion.CHANNEL_MAIN_ID
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment(R.layout.fragment_notification) {

    private val notificationId = 101

    private fun sendNotification( title: String = "Example Title", description : String = "Example Description") {
        val navController = findNavController()

        val pendingIntent = navController
            .createDeepLink()
            .setDestination(R.id.toBuyFragment)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_MAIN_ID)
            .setSmallIcon(R.drawable.ic_notification) // TODO: Заменить иконку на иконку приложения
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, builder.build())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNotificationBinding.bind(view)

        binding.apply {
            notify.setOnClickListener {
                sendNotification(description = "Не забудьте купить продукты!")
            }
        }
    }
}