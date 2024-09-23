package com.example.renotify

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val notification = it.notification
            val extras = notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE)
            val text = extras.getString(Notification.EXTRA_TEXT)

            val notificationData = "$title: $text"

            val intent = Intent(this, NetworkService::class.java).apply {
                putExtra("notification_data", notificationData)
            }
            startService(intent)
        }
    }
}
