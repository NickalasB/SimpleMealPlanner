package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.DayOfWeek.REMOVE
import com.zonkey.simplemealplanner.model.Recipe

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
        view.isSavedRecipe = false
        setSavedRecipeIcon(false)
        view.showRecipeDetailSnackBar(R.string.snackbar_recipe_deleted)
      } else {
        firebaseRepo.saveRecipeAsFavorite(recipe)
        view.isSavedRecipe = true
        setSavedRecipeIcon(true)
        view.showRecipeDetailSnackBar(R.string.snackbar_recipe_saved)
      }
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
          view.addedToMealPlan = true
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
}
