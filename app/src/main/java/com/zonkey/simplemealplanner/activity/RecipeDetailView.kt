package com.zonkey.simplemealplanner.activity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface RecipeDetailView {

  var isSavedRecipe: Boolean

  var addedToMealPlan: Boolean

  fun setFavoritedButtonIcon(@DrawableRes icon: Int)

  fun showRecipeDetailSnackBar(@StringRes snackBarStringRes: Int = 0, snackBarString: String? = "", dayOfWeek: String? = "")

  fun setMealPlanButtonText(@StringRes mealPlanButtonStringRes: Int = 0, selectedDayString: String? = "")

}
