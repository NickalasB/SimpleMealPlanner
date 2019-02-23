package com.zonkey.simplemealplanner.activity

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.DayOfWeek.FRIDAY
import com.zonkey.simplemealplanner.model.DayOfWeek.MONDAY
import com.zonkey.simplemealplanner.model.DayOfWeek.valueOf
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
import org.mockito.internal.verification.Times
import org.mockito.verification.VerificationMode

@RunWith(JUnit4::class)
class RecipeDetailActivityPresenterTest {

  @Mock
  private lateinit var view: RecipeDetailView

  @Mock
  private lateinit var firebaseRecipeRepository: FirebaseRecipeRepository

  private lateinit var presenter: RecipeDetailActivityPresenter

  private lateinit var mockRecipe: Recipe

  private var isSavedRecipe = false

  private var selectedDay = ""

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
    givenRecipe(MONDAY)
    givenSavedRecipe(true)
    whenFavoriteButtonClicked(isSavedRecipe, mockRecipe)
    thenDeleteRecipeFromFirebase(mockRecipe)
    thenSetFavoriteButtonIcon(R.drawable.ic_favorite_border_red_24dp)
    thenShowSnackBar(R.string.snackbar_recipe_deleted)
  }

  @Test
  fun shouldSaveRecipeToFirebaseAndSetSavedRecipeToTrueAndUpdateIconAndShowSnackBarWhenFavoriteButtonClickedIfRecipeNotSaved() {
    givenRecipe(MONDAY)
    givenSavedRecipe(false)
    whenFavoriteButtonClicked(isSavedRecipe, mockRecipe)
    thenSaveRecipeToFirebase(mockRecipe)
    thenIsSavedRecipe(true)
    thenSetFavoriteButtonIcon(R.drawable.ic_favorite_red_24dp)
    thenShowSnackBar(R.string.snackbar_recipe_saved)
  }

  @Test
  fun shouldUpdateMealPlanRecipeDayIfAlreadyAddedToMealPlanDbWhenMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.MONDAY.name)
    givenRecipe(valueOf(selectedDay))
    givenAddedToMealPlanAlready(true)
    givenSavedRecipe(true)
    whenOnMealPlanDialogPositiveButtonClickedCalled(mockRecipe, view.addedToMealPlan, selectedDay,
        isSavedRecipe)
    thenUpdateMealPlanRecipeDayCalled()
  }

  @Test
  fun shouldSaveRecipeToMealPlanDbAndSetAddedToMealPlanTrueIfNotAlreadyAddedToMealPlanDbWhenMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.FRIDAY.name)
    givenRecipe(valueOf(selectedDay))
    givenAddedToMealPlanAlready(false)
    givenSavedRecipe(true)
    whenOnMealPlanDialogPositiveButtonClickedCalled(mockRecipe, view.addedToMealPlan, selectedDay,
        isSavedRecipe)
    thenSaveRecipeToMealPlanDb()
  }

  @Test
  fun shouldSetMealPlanButtonTextToDayIfNotRemovedWhenOnMealPlanDialogPositiveButtonClicked() {
    givenDay(FRIDAY.name)
    givenRecipe(valueOf(selectedDay))
    whenOnMealPlanDialogPositiveButtonClickedCalled(mockRecipe, view.addedToMealPlan,
        selectedDay, isSavedRecipe)
    thenSetMealPlanButtonText(Times(1), selectedDay)
    thenSetMealPlanButtonTextToDefault(never(), R.string.detail_meal_plan_button_text)
  }

  @Test
  fun shouldSetMealPlanButtonTextToDefaultIfRemovedWhenOnMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.REMOVE.name)
    givenRecipe(valueOf(selectedDay))
    whenOnMealPlanDialogPositiveButtonClickedCalled(mockRecipe, view.addedToMealPlan,
        selectedDay, isSavedRecipe)
    thenSetMealPlanButtonTextToDefault(Times(1), R.string.detail_meal_plan_button_text)
    thenSetMealPlanButtonText(never(), selectedDay)
  }

  private fun givenSavedRecipe(isSaved: Boolean) {
    isSavedRecipe = isSaved
  }

  private fun givenRecipe(day: DayOfWeek) {
    mockRecipe = Recipe(
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
        key = "testKey1",
        day = day,
        favorite = true,
        mealPlan = false
    )
  }

  private fun givenAddedToMealPlanAlready(added: Boolean) {
    whenever(view.addedToMealPlan).thenReturn(added)
  }

  private fun givenDay(dayOfWeek: String) {
    selectedDay = dayOfWeek
  }

  private fun whenFavoriteButtonClicked(savedRecipe: Boolean, recipeToSave: Recipe) {
    presenter.onFavoriteButtonClicked(savedRecipe, recipeToSave)
  }

  private fun whenSetSavedRecipeIconCalled(savedRecipe: Boolean) {
    presenter.setSavedRecipeIcon(savedRecipe)
  }

  private fun whenOnMealPlanDialogPositiveButtonClickedCalled(mockRecipe: Recipe,
      addedToMealPlan: Boolean, selectedDay: String,
      savedRecipe: Boolean) {
    presenter.onMealPlanDialogPositiveButtonClicked(mockRecipe, addedToMealPlan, selectedDay,
        savedRecipe)
  }

  private fun thenSetFavoriteButtonIcon(icon: Int) {
    verify(view).setFavoritedButtonIcon(icon)
  }

  private fun thenSaveRecipeToMealPlanDb() {
    verify(firebaseRecipeRepository).saveRecipeToMealPlan(mockRecipe,
        DayOfWeek.valueOf(selectedDay), isSavedRecipe)
  }

  private fun thenUpdateMealPlanRecipeDayCalled() {
    verify(firebaseRecipeRepository).updateMealPlanRecipeDay(mockRecipe,
        DayOfWeek.valueOf(selectedDay))
  }

  private fun thenIsSavedRecipe(isSaved: Boolean) {
    verify(view).isSavedRecipe = isSaved
  }

  private fun thenSaveRecipeToFirebase(recipeToSave: Recipe) {
    verify(firebaseRecipeRepository).saveRecipeAsFavorite(recipeToSave)
  }

  private fun thenDeleteRecipeFromFirebase(recipeToDelete: Recipe) {
    verify(firebaseRecipeRepository).removeRecipeAsFavorite(recipeToDelete)
  }

  private fun thenShowSnackBar(snackBarString: Int) {
    verify(view).showFavoriteSnackBar(snackBarString)
  }

  private fun thenSetMealPlanButtonTextToDefault(times: VerificationMode, defaultStringRes: Int) {
    verify(view, times).setMealPlanButtonText(mealPlanButtonStringRes = defaultStringRes)
  }

  private fun thenSetMealPlanButtonText(times: VerificationMode, selectedDay: String?) {
    verify(view, times).setMealPlanButtonText(selectedDayString = selectedDay)
  }
}