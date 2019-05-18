package com.zonkey.simplemealplanner.utils

import android.view.View
import androidx.annotation.ColorRes
import com.zonkey.simplemealplanner.R

interface UiUtils {

  fun showSnackbar(
      view: View,
      snackbarStringRes: Int = 0,
      snackbarString: String? = "",
      snackbarStringParameter: String? = "",
      @ColorRes backgroundColor: Int = R.color.colorAccent)

}