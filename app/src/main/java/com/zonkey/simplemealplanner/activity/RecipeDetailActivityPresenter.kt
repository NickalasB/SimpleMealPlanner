package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe

class RecipeDetailActivityPresenter(
    private val view: RecipeDetailView,
    private val firebaseRepo: FirebaseRecipeRepository
) {

  fun onFavoriteButtonClicked(savedRecipe: Boolean, recipe: Recipe) {
    if (savedRecipe) {
      firebaseRepo.removeRecipeAsFavorite(recipe)
      view.isSavedRecipe = false
      setSavedRecipeIcon(false)
      view.showFavoriteSnackBar(R.string.snackbar_recipe_deleted)
    } else {
      firebaseRepo.saveRecipeAsFavorite(recipe)
      view.isSavedRecipe = true
      setSavedRecipeIcon(true)
      view.showFavoriteSnackBar(R.string.snackbar_recipe_saved)
    }
  }

  fun setSavedRecipeIcon(savedRecipe: Boolean) {
    if (savedRecipe) {
      view.setFavoritedButtonIcon(R.drawable.ic_favorite_red_24dp)
    } else {
      view.setFavoritedButtonIcon(R.drawable.ic_favorite_border_red_24dp)
    }
  }

  fun onMealPlanDialogPositiveButtonClicked(recipe: Recipe, addedToMealPlan: Boolean, selectedDay: String, isSavedRecipe: Boolean) {
    if (addedToMealPlan) {
      firebaseRepo.updateMealPlanRecipeDay(recipe, DayOfWeek.valueOf(selectedDay))
    } else {
      firebaseRepo.saveRecipeToMealPlan(recipe, DayOfWeek.valueOf(selectedDay), isSavedRecipe)
      view.addedToMealPlan = true
    }
  }
}
