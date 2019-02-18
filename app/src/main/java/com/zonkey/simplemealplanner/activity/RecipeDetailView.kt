package com.zonkey.simplemealplanner.activity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface RecipeDetailView {
  var isSavedRecipe: Boolean

  fun setFavoritedButtonIcon(@DrawableRes icon: Int)

  fun showFavoriteSnackBar(@StringRes snackBarString: Int)

}
