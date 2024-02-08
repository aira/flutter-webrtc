package com.cloudwebrtc.webrtc;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat


class MediaProjectionService : Service() {
  private val foregroundNotificationId = 1

  override fun onCreate() {
    super.onCreate()
    println(">>>>> MediaProjectionService.onCreate")
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    println(">>>>> MediaProjectionService.onStartCommand 1")
    val title = intent?.getStringExtra("notificationTitle")
    val text = intent?.getStringExtra("notificationText")

    // Create and display the foreground notification
    // Create a notification channel (required for Android Oreo and above)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        "media_projection_channel",
        "Media Projection Channel",
        NotificationManager.IMPORTANCE_DEFAULT
      )
      val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
    println(">>>>> MediaProjectionService.onStartCommand 2")

    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
    val yourPendingIntent = PendingIntent.getActivity(
      this,
      0,
      launchIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    println(">>>>> MediaProjectionService.onStartCommand 3")
    // Create a foreground notification
    val notificationBuilder =
      NotificationCompat.Builder(this, "media_projection_channel")
//        .setSmallIcon(R.mipmap.ic_launcher)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(yourPendingIntent)
    if (null != title) {
      notificationBuilder.setContentTitle(title)
    }
    if (null != text) {
      notificationBuilder.setContentText(text)
    }

    // Start the service in the foreground with the notification
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      println(">>>>> MediaProjectionService.onStartCommand 4a")
      startForeground(
        foregroundNotificationId,
        notificationBuilder.build(),
        FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
      )
    } else {
      println(">>>>> MediaProjectionService.onStartCommand 4b")
      startForeground(
        foregroundNotificationId,
        notificationBuilder.build()
      )
    }

    // Return START_STICKY to ensure the service restarts if killed by the system
    return START_NOT_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onDestroy() {
    println(">>>>> MediaProjectionService.onDestroy 1")
    super.onDestroy()

    // Stop the foreground service and remove the notification
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
    println(">>>>> MediaProjectionService.onDestroy 2")
  }
}
