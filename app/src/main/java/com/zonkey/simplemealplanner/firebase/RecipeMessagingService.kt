package com.zonkey.simplemealplanner.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat.Builder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.zonkey.simplemealplanner.activity.RecipeDetailActivity
import com.zonkey.simplemealplanner.model.Recipe
import timber.log.Timber


private const val NOTIFICATION_CHANNEL_ID = "RecipeNotifications"
internal const val NOTIFICATION_FULL_RECIPE = "notification_recipe"
private const val REMOTE_MESSAGE_TITLE_DATA = "title"
private const val REMOTE_MESSAGE_BODY_DATA = "body"
private const val REMOTE_MESSAGE_IMAGE_DATA = "image"
private const val REMOTE_MESSAGE_RECIPE_DATA = "recipeJson"

class RecipeMessagingService : FirebaseMessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage?) {

    remoteMessage?.let {
      val notificationTitle = String.format(
          getString(com.zonkey.simplemealplanner.R.string.shared_recipes_notification_title,
              remoteMessage.data[REMOTE_MESSAGE_TITLE_DATA]))
      val notificationBody = String.format(
          getString(com.zonkey.simplemealplanner.R.string.shared_recipes_notification_body,
              remoteMessage.data[REMOTE_MESSAGE_BODY_DATA]))
      val notificationImageUrl = remoteMessage.data[REMOTE_MESSAGE_IMAGE_DATA]
      val recipe = remoteMessage.data[REMOTE_MESSAGE_RECIPE_DATA]

      Gson().fromJson<Recipe>(remoteMessage.data[REMOTE_MESSAGE_RECIPE_DATA], Recipe::class.java)

      buildNotification(notificationTitle, notificationBody, notificationImageUrl, recipe)
    }
  }

  private fun buildNotification(
      notificationTitle: String?,
      notificationBody: String?,
      notificationImageUrl: String?,
      recipe: String?) {

    val notificationChannelName = getString(
        com.zonkey.simplemealplanner.R.string.shared_recipes_notification_channel_name)

    val notificationBuilder = if (VERSION.SDK_INT >= VERSION_CODES.O) {
      val notificationChannel = NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          notificationChannelName,
          IMPORTANCE_DEFAULT)
      Builder(this, notificationChannel.id)
    } else {
      @Suppress("DEPRECATION")
      Builder(this)
    }

    val intent = Intent(this, RecipeDetailActivity::class.java)
    intent.putExtra(NOTIFICATION_FULL_RECIPE, recipe)

    val pendingIntent = TaskStackBuilder.create(this)
        .addNextIntentWithParentStack(intent)
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    notificationBuilder
        .setSmallIcon(com.zonkey.simplemealplanner.R.mipmap.ic_launcher)
        .setContentTitle(notificationTitle)
        .setContentText(notificationBody)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)

    Glide.with(this)
        .asBitmap()
        .load(notificationImageUrl)
        .listener(object : RequestListener<Bitmap> {
          override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?,
              isFirstResource: Boolean): Boolean {
            //Send the notification but just don't set the large icon if we fail to load bitmap
            sendNotification(notificationBuilder)
            Timber.e(e, "Problem loading notification bitmap")
            return false
          }

          override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?,
              dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            notificationBuilder.setLargeIcon(resource)
            sendNotification(notificationBuilder)
            return true
          }
        }).submit()
  }

  private fun sendNotification(notificationBuilder: Builder) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        .notify(0, notificationBuilder.build())
  }
}