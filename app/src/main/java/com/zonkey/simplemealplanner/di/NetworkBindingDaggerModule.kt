package com.zonkey.simplemealplanner.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zonkey.simplemealplanner.network.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


private const val CACHE_SIZE = 10 * 1024 * 1024 //10MiB

@Module
class NetworkBindingDaggerModule {

  @Provides
  @Singleton
  fun provideOkHttpCache(application: Application): Cache =
      Cache(application.cacheDir, CACHE_SIZE.toLong())

  @Provides
  @Singleton
  fun provideGson(): Gson = GsonBuilder().create()

  @Provides
  @Singleton
  fun provideGsonConverterFactory(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)

  @Provides
  @Singleton
  fun provideOkHttpClient(cache: Cache): OkHttpClient =
      OkHttpClient.Builder().cache(cache).build()

  @Provides
  @Singleton
  fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .build()
}