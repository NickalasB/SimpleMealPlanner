package com.zonkey.simplemealplanner

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.zonkey.simplemealplanner.di.AppComponent
import com.zonkey.simplemealplanner.di.AppDaggerModule
import com.zonkey.simplemealplanner.di.DaggerAppComponent
import com.zonkey.simplemealplanner.network.RecipeService
import com.zonkey.simplemealplanner.network.RetrofitInstance
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

internal const val RECIPE_SHARE_NOTIFICATION_CHANNEL_ID = "RecipeShareNotifications"

class RecipeApp : Application(), HasActivityInjector {

  companion object {
    lateinit var instance: RecipeApp
      private set
  }

  @JvmField
  @Inject
  @Transient
  var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>? = null

  @Transient
  private lateinit var appComponent: AppComponent

  override fun activityInjector(): AndroidInjector<Activity>? = dispatchingActivityInjector

  override fun onCreate() {
    super.onCreate()
    instance = this

    initializeFirebase()
    setUpDagger()
    initializeApiClient()
    createNotificationChannels()
  }

  private fun createNotificationChannels() {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      val notificationChannelName = getString(R.string.shared_recipes_notification_channel_name)
      val notificationManager = getSystemService(
          Context.NOTIFICATION_SERVICE) as NotificationManager
      val notificationChannel = NotificationChannel(
          RECIPE_SHARE_NOTIFICATION_CHANNEL_ID,
          notificationChannelName,
          NotificationManager.IMPORTANCE_DEFAULT)
      notificationManager.createNotificationChannel(notificationChannel)
    }
  }

  private fun initializeFirebase() {
    FirebaseApp.initializeApp(this)
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
  }

  private fun setUpDagger() {
    appComponent = DaggerAppComponent.builder()
        .appDaggerModule(AppDaggerModule(this))
        .build()
    appComponent.inject(this)
  }

  private fun initializeApiClient() {
    RetrofitInstance().getRetrofitInstance().create(
        RecipeService::class.java)
  }
}