package com.zonkey.simplemealplanner.di

import android.app.Application
import android.content.Context
import com.zonkey.simplemealplanner.utils.DefaultUiUtils
import com.zonkey.simplemealplanner.utils.UiUtils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppDaggerModule(@Transient private val application: Application) {

  @Provides
  @Singleton
  fun providesApplication(): Application = application

  @Provides
  @Singleton
  fun providesContext(): Context = application

  @Provides
  @Singleton
  fun providesUiUtils(): UiUtils = DefaultUiUtils()
}