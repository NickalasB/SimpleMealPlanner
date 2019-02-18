package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.Recipe

class RecipeDetailActivityPresenter(
    private val view: RecipeDetailView,
    private val firebaseRepo: FirebaseRecipeRepository
) {

  fun onFavoriteButtonClicked(savedRecipe: Boolean, recipe: Recipe) {
    if (savedRecipe) {
      firebaseRepo.deleteRecipeFromFirebase(recipe)
      setSavedRecipeIcon(false)
      view.showFavoriteSnackBar(R.string.snackbar_recipe_deleted)
    } else {
      firebaseRepo.saveRecipeToFirebase(recipe)
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
}
