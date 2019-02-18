package com.zonkey.simplemealplanner.activity

import com.nhaarman.mockitokotlin2.verify
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.Diet
import com.zonkey.simplemealplanner.model.Ingredient
import com.zonkey.simplemealplanner.model.NutrientInfo
import com.zonkey.simplemealplanner.model.Recipe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class RecipeDetailActivityPresenterTest {

  @Mock
  private lateinit var view: RecipeDetailView

  @Mock
  private lateinit var firebaseRecipeRepository: FirebaseRecipeRepository

  private lateinit var presenter: RecipeDetailActivityPresenter

  private lateinit var recipeToSave: Recipe

  private var isSavedRecipe = false

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)

    presenter = RecipeDetailActivityPresenter(view, firebaseRecipeRepository)
  }

  @Test
  fun shouldSetFavoriteIconToOutlinedHeartWhenRecipeNotSaved() {
    givenSavedRecipe(false)
    whenSetSavedRecipeIconCalled(savedRecipe = isSavedRecipe)
    thenSetFavoriteButtonIcon(R.drawable.ic_favorite_border_red_24dp)
  }

  @Test
  fun shouldSetFavoriteIconToFilledInHeartWhenRecipeIsSaved() {
    givenSavedRecipe(true)
    whenSetSavedRecipeIconCalled(savedRecipe = isSavedRecipe)
    thenSetFavoriteButtonIcon(R.drawable.ic_favorite_red_24dp)
  }

  @Test
  fun shouldDeleteRecipeFromFirebaseAndSetSavedRecipeToFalseAndUpdateIconAndShowSnackBarWhenFavoriteButtonClickedIfRecipeSaved() {
    givenRecipeToSave()
    givenSavedRecipe(true)
    whenFavoriteButtonClicked(isSavedRecipe, recipeToSave)
    thenDeleteRecipeFromFirebase(recipeToSave)
    thenSetFavoriteButtonIcon(R.drawable.ic_favorite_border_red_24dp)
    thenShowSnackBar(R.string.snackbar_recipe_deleted)
  }

  @Test
  fun shouldSaveRecipeToFirebaseAndSetSavedRecipeToTrueAndUpdateIconAndShowSnackBarWhenFavoriteButtonClickedIfRecipeNotSaved() {
    givenRecipeToSave()
    givenSavedRecipe(false)
    whenFavoriteButtonClicked(isSavedRecipe, recipeToSave)
    thenSaveRecipeToFirebase(recipeToSave)
    thenSetFavoriteButtonIcon(R.drawable.ic_favorite_red_24dp)
    thenShowSnackBar(R.string.snackbar_recipe_saved)
  }

  private fun thenSaveRecipeToFirebase(recipeToSave: Recipe) {
    verify(firebaseRecipeRepository).saveRecipeToFirebase(recipeToSave)
  }

  private fun whenFavoriteButtonClicked(savedRecipe: Boolean, recipeToSave: Recipe) {
    presenter.onFavoriteButtonClicked(savedRecipe, recipeToSave)
  }

  private fun thenDeleteRecipeFromFirebase(recipeToDelete: Recipe) {
    verify(firebaseRecipeRepository).deleteRecipeFromFirebase(recipeToDelete)
  }

  private fun thenShowSnackBar(snackBarString: Int) {
    verify(view).showFavoriteSnackBar(snackBarString)
  }

  private fun givenSavedRecipe(isSaved: Boolean) {
    isSavedRecipe = isSaved
  }

  private fun givenRecipeToSave() {
    recipeToSave = Recipe(
        uri = "testUri1",
        label = "testLabel1",
        image = "testImage1",
        source = "testSource1",
        url = "testUrl1",
        `yield` = 0f,
        dietLabels = emptyList<Diet>(),
        healthLabels = emptyList<Diet>(),
        ingredientLines = emptyList<String>(),
        ingredients = emptyList<Ingredient>(),
        calories = 0f,
        totalWeight = 0f,
        totalNutrients = NutrientInfo(),
        totalDaily = NutrientInfo(),
        key = "testKey1"
    )
  }

  private fun whenSetSavedRecipeIconCalled(savedRecipe: Boolean) {
    presenter.setSavedRecipeIcon(savedRecipe)
  }

  private fun thenSetFavoriteButtonIcon(icon: Int) {
    verify(view).setFavoritedButtonIcon(icon)
  }

}