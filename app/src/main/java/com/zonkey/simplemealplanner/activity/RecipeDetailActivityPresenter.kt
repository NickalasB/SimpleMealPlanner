package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseAuthRepository
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.DayOfWeek.REMOVE
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.model.User

private const val FAVORITE_BUTTON_FORWARD_SPEED = 1f
private const val FAVORITE_BUTTON_BACKWARDS_SPEED = -1f //setting speed to -1 reverses animation

class RecipeDetailActivityPresenter(
    private val view: RecipeDetailView,
    private val firebaseRepo: FirebaseRecipeRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
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
        firebaseRepo.saveUserIdEmailAndMessagingToken()
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
              .addOnSuccessListener {
                view.addedToMealPlan = true
                view.setUpShareButton(view.contactPermissionGranted, view.addedToMealPlan)
                setShareButtonBackground(view.addedToMealPlan)
              }
        }
      }
      when (DayOfWeek.valueOf(selectedDay)) {
        REMOVE -> {
          view.setMealPlanButtonText(
              mealPlanButtonStringRes = R.string.detail_meal_plan_button_text)
          firebaseRepo.removeRecipeFromMealPlan(recipe).addOnSuccessListener {
            view.addedToMealPlan = false
            view.setUpShareButton(view.contactPermissionGranted, view.addedToMealPlan)
            setShareButtonBackground(view.addedToMealPlan)
          }
        }
        else -> view.setMealPlanButtonText(selectedDayString = selectedDay)
      }
      showRecipeDetailSnackBar(selectedDay)
      firebaseRepo.saveUserIdEmailAndMessagingToken()
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

  fun saveRecipeToSharedDB(
      userToShareWith: User?,
      recipe: Recipe,
      dayOfWeek: DayOfWeek,
      destinationUserName: String?,
      destinationEmail: String) {

    if (userToShareWith != null) {
      val sharedRecipe = recipe.copy(
          fromShare = true,
          sharedFromUser = firebaseAuthRepository.currentUser?.displayName ?: "A friend")
      firebaseRepo.saveRecipeToSharedDB(
          userId = userToShareWith.userId,
          recipe = sharedRecipe,
          dayOfWeek = dayOfWeek)
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
        view.showFavoriteButtonAndMealPlanButtonTutorialCircles()
        view.setIsFirstTimeInActivity(false)
      }
    }

  }

  fun showShareButtonTutorial(addedToMealPlan: Boolean, hasSeenShareButtonTutorial: Boolean) {
    if (addedToMealPlan && !hasSeenShareButtonTutorial) {
      view.showShareButtonTutorialCircle()
    }
  }

  fun setShareButtonBackground(isMealPlanRecipe: Boolean) {
    if (isMealPlanRecipe) {
      view.setShareButtonBackground(R.drawable.ic_share_index_blue_24dp)
    } else {
      view.setShareButtonBackground(R.drawable.ic_share_disabled_24dp)
    }
  }

  fun onShareButtonClicked(permissionGranted: Boolean, savedToMealPlanAlready: Boolean) {
    when (savedToMealPlanAlready) {
      true -> {
        when (permissionGranted) {
          true -> view.launchContactPicker()
          false -> view.handlePermissionRequest()
        }
      }
      false -> {
        view.showRecipeDetailSnackBar(
            snackBarStringRes = R.string.disabled_share_button_snackbar_message)
        return
      }
    }
  }
}
