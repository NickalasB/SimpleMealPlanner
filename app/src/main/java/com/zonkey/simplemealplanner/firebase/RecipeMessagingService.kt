package com.zonkey.simplemealplanner.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.RECIPE_SHARE_NOTIFICATION_CHANNEL_ID
import com.zonkey.simplemealplanner.activity.RecipeDetailActivity
import com.zonkey.simplemealplanner.model.Recipe
import timber.log.Timber


internal const val RECIPE_FROM_NOTIFICATION = "recipeFromNotification"
private const val REMOTE_MESSAGE_RECIPE_DATA = "notificationRecipe"

class RecipeMessagingService : FirebaseMessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage?) {

    remoteMessage?.let {

      val recipeJsonString = remoteMessage.data[REMOTE_MESSAGE_RECIPE_DATA]

      val sharedRecipe = Gson().fromJson<Recipe>(recipeJsonString, Recipe::class.java)

      val notificationTitle = String.format(
          getString(com.zonkey.simplemealplanner.R.string.shared_recipes_notification_title,
              sharedRecipe.sharedFromUser))

      val notificationBody = String.format(
          getString(com.zonkey.simplemealplanner.R.string.shared_recipes_notification_body,
              sharedRecipe.label))

      val notificationImageUrl = sharedRecipe.image

      val mealPlanDay = sharedRecipe.day.name.toLowerCase().capitalize()

      buildNotification(notificationTitle, notificationBody, notificationImageUrl, recipeJsonString,
          mealPlanDay)
    }
  }

  private fun buildNotification(
      notificationTitle: String?,
      notificationBody: String?,
      notificationImageUrl: String?,
      recipeJsonString: String?,
      day: String) {


    val notificationBuilder = if (VERSION.SDK_INT >= VERSION_CODES.O) {
      Builder(this, RECIPE_SHARE_NOTIFICATION_CHANNEL_ID)
    } else {
      @Suppress("DEPRECATION")
      Builder(this)
    }

    val intent = Intent(this, RecipeDetailActivity::class.java)
    intent.putExtra(RECIPE_FROM_NOTIFICATION, recipeJsonString)

    val pendingIntent = TaskStackBuilder.create(this)
        .addNextIntentWithParentStack(intent)
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    notificationBuilder
        .setSmallIcon(R.drawable.ic_notifcation_icon_primary_24dp)
        .setContentTitle(notificationTitle)
        .setContentText(notificationBody)
        .setSound(defaultSoundUri)
        .setChannelId(RECIPE_SHARE_NOTIFICATION_CHANNEL_ID)
        .setContentIntent(pendingIntent)
        .priority = (PRIORITY_DEFAULT)

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
            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(resource)
                .setSummaryText(
                    String.format(getString(R.string.shared_recipes_notification_big_summary_text),
                        day))
                .bigLargeIcon(null))
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