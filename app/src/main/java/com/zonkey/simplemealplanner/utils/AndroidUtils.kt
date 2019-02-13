package com.zonkey.simplemealplanner.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

inline fun <reified V : View> ViewGroup.inflate(@LayoutRes resId: Int, attach: Boolean = true): V =
    LayoutInflater.from(context).inflate(resId, this, attach) as V

fun createBitmapFromDrawableRes(
    context: Context,
    resId: Int): Bitmap? {
  val drawableResource = ContextCompat.getDrawable(context, resId)
  drawableResource?.let { drawable ->
    drawable.setBounds(
        0,
        0,
        drawable.intrinsicWidth,
        drawable.intrinsicHeight)

    val bitMap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitMap)
    drawable.draw(canvas)
    return bitMap
  }
  return null
}
