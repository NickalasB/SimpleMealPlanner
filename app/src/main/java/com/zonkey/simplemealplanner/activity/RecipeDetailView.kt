package com.zonkey.simplemealplanner.activity

import androidx.annotation.StringRes

interface RecipeDetailView {

  var isSavedRecipe: Boolean

  var addedToMealPlan: Boolean

  fun setFavoritedButtonAnimationDirection(speed: Float)

  fun showRecipeDetailSnackBar(@StringRes snackBarStringRes: Int = 0, snackBarString: String? = "", dayOfWeek: String? = "")

  fun setMealPlanButtonText(@StringRes mealPlanButtonStringRes: Int = 0, selectedDayString: String? = "")

  fun launchUIAuthActivity()

  fun showSnackbar(@StringRes snackbarStringRes: Int = 0, snackbarString: String = "", snackbarstringParameter: String = "")

  fun showFavoriteButtonTutorialCircle()

  fun setIsFirstTimeInActivity(isFirstTime: Boolean)
}
