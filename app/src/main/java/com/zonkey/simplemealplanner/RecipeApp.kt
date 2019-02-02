package com.zonkey.simplemealplanner

import android.app.Activity
import android.app.Application
import com.google.firebase.FirebaseApp
import com.zonkey.simplemealplanner.di.AppComponent
import com.zonkey.simplemealplanner.di.AppDaggerModule
import com.zonkey.simplemealplanner.di.DaggerAppComponent
import com.zonkey.simplemealplanner.network.RecipeService
import com.zonkey.simplemealplanner.network.RetrofitInstance
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class RecipeApp : Application(),
    HasActivityInjector {

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

    FirebaseApp.initializeApp(this)

    appComponent = DaggerAppComponent.builder()
        .appDaggerModule(AppDaggerModule(this))
        .build()

    appComponent.inject(this)

    initializeApiClient()
  }

  private fun initializeApiClient() {
    RetrofitInstance().getRetrofitInstance().create(
        RecipeService::class.java)
  }
}