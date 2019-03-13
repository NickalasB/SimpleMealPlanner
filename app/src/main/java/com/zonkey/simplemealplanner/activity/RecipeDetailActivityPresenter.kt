package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.DayOfWeek.REMOVE
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.model.User
import timber.log.Timber

class RecipeDetailActivityPresenter(
    private val view: RecipeDetailView,
    private val firebaseRepo: FirebaseRecipeRepository
) {

  fun onFavoriteButtonClicked(isSignedIn: Boolean, savedRecipe: Boolean, recipe: Recipe) {
    if (!isSignedIn) {
      view.launchUIAuthActivity()
    } else {
      if (savedRecipe) {
        firebaseRepo.removeRecipeAsFavorite(recipe)
            .addOnSuccessListener {
              view.isSavedRecipe = false
              setSavedRecipeIcon(false)
              view.showRecipeDetailSnackBar(R.string.snackbar_recipe_deleted)
            }
      } else {
        saveFirsTimeUserRecipe(recipe)
      }
    }
  }

  private fun saveFirsTimeUserRecipe(recipe: Recipe) {
    firebaseRepo.saveUserIdAndUserEmail()
        .continueWith {
          firebaseRepo.saveRecipeAsFavorite(recipe)
        }.addOnSuccessListener {
          view.isSavedRecipe = true
          setSavedRecipeIcon(true)
          view.showRecipeDetailSnackBar(R.string.snackbar_recipe_saved)
          Timber.d("User logged in from saveFirsTimeUserRecipe()")
        }
  }

  fun setSavedRecipeIcon(savedRecipe: Boolean) {
    if (savedRecipe) {
      view.setFavoritedButtonIcon(R.drawable.ic_favorite_red_24dp)
    } else {
      view.setFavoritedButtonIcon(R.drawable.ic_favorite_border_red_24dp)
    }
  }

  fun onMealPlanDialogPositiveButtonClicked(
      isSignedIn: Boolean,
      recipe: Recipe,
      addedToMealPlan: Boolean,
      selectedDay: String,
      isSavedRecipe: Boolean) {
    if (!isSignedIn) {
      view.launchUIAuthActivity()
    } else {
      when {
        addedToMealPlan -> firebaseRepo.updateMealPlanRecipeDay(recipe,
            DayOfWeek.valueOf(selectedDay))
        else -> {
          firebaseRepo.saveRecipeToMealPlan(recipe, DayOfWeek.valueOf(selectedDay), isSavedRecipe)
              .addOnSuccessListener {
                view.addedToMealPlan = true
              }
        }
      }
      when (DayOfWeek.valueOf(selectedDay)) {
        REMOVE -> {
          view.setMealPlanButtonText(
              mealPlanButtonStringRes = R.string.detail_meal_plan_button_text)
          firebaseRepo.removeRecipeFromMealPlan(recipe)
        }
        else -> view.setMealPlanButtonText(selectedDayString = selectedDay)
      }
      showRecipeDetailSnackBar(selectedDay)
      firebaseRepo.saveUserIdAndUserEmail()
          .addOnSuccessListener {
            Timber.d("User logged in from saveFirsTimeUserRecipe()")
          }
    }
  }

  fun showRecipeDetailSnackBar(selectedDay: String) {
    when (selectedDay) {
      REMOVE.name -> view.showRecipeDetailSnackBar(
          snackBarStringRes = R.string.detail_snackbar_meal_plan_removed)
      else -> view.showRecipeDetailSnackBar(
          snackBarStringRes = R.string.detail_meal_plan_snackbar_text, dayOfWeek = selectedDay)
    }
  }

  fun setUpMealPlanButtonText(recipe: Recipe) {
    if (recipe.mealPlan && recipe.day.name.isNotEmpty()) {
      view.setMealPlanButtonText(selectedDayString = recipe.day.name)
    }
  }

  fun saveRecipeToSharedDB(userToShareWith: User?, recipe: Recipe, destinationUserName: String?,
      destinationEmail: String) {
    if (userToShareWith != null) {
      firebaseRepo.saveRecipeToSharedDB(userToShareWith.userId, recipe, recipe.day)
          .addOnSuccessListener {
            view.showSnackbar(
                snackbarStringRes = R.string.share_snackbar_success_text,
                snackbarstringParameter = destinationUserName ?: destinationEmail)
          }
          .addOnFailureListener {
            Timber.e(
                "Failed to share recipe from ${this@RecipeDetailActivityPresenter::class.java.simpleName}")
            view.showSnackbar(snackbarStringRes = R.string.share_snackbar_error_text)
          }
      return
    } else {
      view.showSnackbar(
          snackbarStringRes = R.string.share_recipe_snackbar_user_not_registered,
          snackbarstringParameter = destinationUserName ?: destinationEmail)
    }
  }
}
