package com.zonkey.simplemealplanner.utils

import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class DefaultUiUtils : UiUtils {

  override fun showSnackbar(
      view: View,
      snackbarStringRes: Int,
      snackbarString: String?,
      snackbarStringParameter: String?,
      @ColorRes backgroundColor: Int) {

    val snackBarText = when {
      snackbarStringRes != 0 -> view.context.getString(snackbarStringRes, snackbarStringParameter)
      !snackbarString.isNullOrEmpty() -> snackbarString
      else -> "Something is not quite right. Please try again"
    }
    val snackbar = Snackbar.make(view, snackBarText, Snackbar.LENGTH_LONG)
    snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, backgroundColor))
    snackbar.show()
  }
}