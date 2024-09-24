package com.example.renotify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Socket

class NetworkService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private val notificationId = 1
    private val channelId = "NetworkServiceChannel"

    override fun onCreate() {
        super.onCreate()

        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationData = intent?.getStringExtra("notification_data")

        serviceScope.launch {
            notificationData?.let {
                sendNotificationData(it)
            }
        }

        return START_STICKY
    }

    private suspend fun sendNotificationData(data: String) {
        try {
            withContext(Dispatchers.IO) {
                val socket = Socket("192.168.0.103", 8080)
                socket.getOutputStream().write(data.toByteArray())
                socket.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startForegroundService() {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Notification Service Running")
            .setContentText("Listening for incoming notifications...")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Notification Listener Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.launch {
            try {
                val message = "Exit"
                val socket = Socket("192.168.0.103", 8080)
                val outputStream = socket.getOutputStream()
                outputStream.write(message.toByteArray())
                outputStream.flush()
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
