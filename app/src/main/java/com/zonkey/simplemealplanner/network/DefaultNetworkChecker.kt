package com.zonkey.simplemealplanner.network

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class DefaultNetworkChecker @Inject constructor(private val context: Context) : NetworkChecker {


  override fun internetIsAvailable(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.activeNetworkInfo?.apply {
      return isConnected
    }
    return false
  }
}

//    try {
//      activeNetworkInfo = connectivityManager.activeNetworkInfo
//    } catch (e: RemoteException) {
//      return false
//    }
//    return activeNetworkInfo != null && activeNetworkInfo.isConnected == true
//  }
//}