package com.zonkey.simplemealplanner.network

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class NetworkConnectionInterceptor @Inject constructor(
    private val networkChecker: NetworkChecker) : Interceptor {

  override fun intercept(chain: Chain): Response {
    val request = chain.request()
    if (!networkChecker.internetIsAvailable()) {
      throw NetworkConnectivityException()
    }
    return chain.proceed(request)
  }
}