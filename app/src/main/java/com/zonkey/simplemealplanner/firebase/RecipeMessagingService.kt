package com.zonkey.simplemealplanner.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.R.mipmap
import com.zonkey.simplemealplanner.activity.MainActivity

const val NOTIFICATION_CHANNEL_ID = "RecipeNotifications"

class RecipeMessagingService : FirebaseMessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage?) {

    remoteMessage?.let {
      val notificationTitle = String.format(getString(R.string.shared_recipes_notification_title,
          remoteMessage.notification?.title))
      val notificationBody = String.format(getString(R.string.shared_recipes_notification_body,
          remoteMessage.notification?.body))
      sendNotification(notificationTitle, notificationBody)
    }
  }

  private fun sendNotification(notificationTitle: String?, notificationBody: String?) {
    val intent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationChannelName = getString(R.string.shared_recipes_notification_channel_name)

    val notificationBuilder = if (VERSION.SDK_INT >= VERSION_CODES.O) {
      val notificationChannel = NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          notificationChannelName,
          IMPORTANCE_DEFAULT)
      NotificationCompat.Builder(this, notificationChannel.id)
    } else {
      NotificationCompat.Builder(this)
    }

    notificationBuilder
        .setSmallIcon(mipmap.ic_launcher)
        .setContentTitle(notificationTitle)
        .setContentText(notificationBody)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(0, notificationBuilder.build())
  }
}
