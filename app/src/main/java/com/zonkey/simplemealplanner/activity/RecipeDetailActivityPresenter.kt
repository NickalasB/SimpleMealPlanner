package com.zonkey.simplemealplanner.activity

import android.view.View
import com.google.android.gms.tasks.Task
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.DayOfWeek.REMOVE
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.model.User

private const val FAVORITE_BUTTON_FORWARD_SPEED = 1f
private const val FAVORITE_BUTTON_BACKWARDS_SPEED = -1f //setting speed to -1 reverses animation

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
        firebaseRepo.saveUserIdAndUserEmail()
        firebaseRepo.saveRecipeAsFavorite(recipe)
        view.isSavedRecipe = true
        setSavedRecipeIcon(true)
        view.showRecipeDetailSnackBar(R.string.snackbar_recipe_saved)
      }
    }
  }

  fun setSavedRecipeIcon(savedRecipe: Boolean) {
    if (savedRecipe) {
      view.setFavoritedButtonAnimationDirection(FAVORITE_BUTTON_FORWARD_SPEED)
    } else {
      view.setFavoritedButtonAnimationDirection(FAVORITE_BUTTON_BACKWARDS_SPEED)
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
      firebaseRepo.saveUserIdAndUserEmail()
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

  fun setupMealPlanButtonText(recipe: Recipe) {
    if (recipe.mealPlan && recipe.day.name.isNotEmpty()) {
      view.setMealPlanButtonText(selectedDayString = recipe.day.name)
    }
  }

  fun saveRecipeToSharedDB(userToShareWith: User?, recipe: Recipe, destinationUserName: String?,
      destinationEmail: String) {

    if (userToShareWith != null) {
      val action: List<Task<Void>> = firebaseRepo.saveRecipeToSharedDB(userToShareWith.userId, recipe, recipe.day)

          action.last()
          .addOnSuccessListener {
            view.showSnackbar(
                snackbarStringRes = R.string.share_snackbar_success_text,
                snackbarstringParameter = destinationUserName ?: destinationEmail)
          }
      return
    } else {
      view.showSnackbar(
          snackbarStringRes = R.string.share_recipe_snackbar_user_not_registered,
          snackbarstringParameter = destinationUserName ?: destinationEmail)
    }
  }

  fun setUpFavoriteButton(isSavedRecipe: Boolean, firstTimeInActivity: Boolean) {
    if (isSavedRecipe) {
      setSavedRecipeIcon(isSavedRecipe)
    } else {
      if (firstTimeInActivity) {
        view.showFavoriteButtonTutorialCircle()
        view.setIsFirstTimeInActivity(false)
      }
    }

  }

  fun showShareButtonTutorial(savedRecipe: Boolean, hasSeenShareButtonTutorial: Boolean) {
    if (savedRecipe && !hasSeenShareButtonTutorial) {
      view.showShareButtonTutorialCircle()
    }
  }

  fun setupShareButton(recipe: Recipe) {
    if(recipe.favorite || recipe.mealPlan) {
      view.setShareButtonVisibility(View.VISIBLE)
    } else {
      view.setShareButtonVisibility(View.GONE)
    }

  }

  fun onShareButtonClicked(permissionGranted: Boolean) {
    if (!permissionGranted) {
      view.handlePermissionRequest()
    } else {
      view.launchContactPicker()
    }
  }
}
