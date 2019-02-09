package com.zonkey.simplemealplanner.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

inline fun <reified V : View> ViewGroup.inflate(@LayoutRes resId: Int, attach: Boolean = true) : V =
    LayoutInflater.from(context).inflate(resId, this, attach) as V