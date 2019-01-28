package com.zonkey.simplemealplanner.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

  fun getRetrofitInstance(): Retrofit = Retrofit.Builder()
      .baseUrl(EDAMAM_BASE_URL)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
}