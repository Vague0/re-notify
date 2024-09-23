package com.example.renotify

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket

class NetworkService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val notificationData = it.getStringExtra("notification_data")

            serviceScope.launch {
                sendNotificationData(notificationData)
            }
        }

        return START_NOT_STICKY
    }

    private fun sendNotificationData(notificationData: String?) {
        notificationData?.let {
            val receiverIP = "192.168.0.103"
            val port = 8080

            try {
                val inetAddress = InetAddress.getByName(receiverIP)
                val socket = Socket(inetAddress, port)

                val writer = OutputStreamWriter(socket.getOutputStream())
                writer.write(notificationData)
                writer.flush()

                socket.close()
                Log.d("NetworkService", "Notification sent successfully")
            } catch (e: Exception) {
                Log.e("NetworkService", "Error sending data", e)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.launch {
            // Todo
        }
    }
}
