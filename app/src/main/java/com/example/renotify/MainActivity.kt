package com.example.renotify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var isServiceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val requestPermissionButton: Button = findViewById(R.id.request_permission_button)

        requestPermissionButton.setOnClickListener {
            val packageNames = NotificationManagerCompat.getEnabledListenerPackages(this)
            if (!packageNames.contains(packageName)) {
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            }

            isServiceRunning = !isServiceRunning
            NotificationListener.isSendingEnabled = isServiceRunning

            if (!isServiceRunning) {
                stopService(Intent(this, NetworkService::class.java))
            }

        }
    }
}