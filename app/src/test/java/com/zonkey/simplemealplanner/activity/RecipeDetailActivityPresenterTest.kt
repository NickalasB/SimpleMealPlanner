package com.zonkey.simplemealplanner.activity

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.DayOfWeek.FRIDAY
import com.zonkey.simplemealplanner.model.DayOfWeek.MONDAY
import com.zonkey.simplemealplanner.model.DayOfWeek.REMOVE
import com.zonkey.simplemealplanner.model.DayOfWeek.TUESDAY
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
    thenSetFavoriteButtonIcon(Times(1), R.drawable.ic_favorite_border_red_24dp)
  }

  @Test
  fun shouldSetFavoriteIconToFilledInHeartWhenRecipeIsSaved() {
    givenSavedRecipe(true)
    whenSetSavedRecipeIconCalled(savedRecipe = isSavedRecipe)
    thenSetFavoriteButtonIcon(Times(1), R.drawable.ic_favorite_red_24dp)
  }

  @Test
  fun shouldLaunchUIAuthActivityWhenNotLoggedInWhenFavoriteButtonClicked() {
    givenRecipe(MONDAY)
    givenSavedRecipe(true)
    whenFavoriteButtonClicked(false, isSavedRecipe, mockRecipe)
    theLaunchUiAuthActivity(Times(1))
    thenDeleteRecipeFromFirebase(never(), mockRecipe)
    thenSetFavoriteButtonIcon(never(), R.drawable.ic_favorite_border_red_24dp)
    thenShowSnackBar(never(), R.string.snackbar_recipe_deleted)
  }

  private fun theLaunchUiAuthActivity(times: Times) {
    verify(view, times).launchUIAuthActivity()
  }

  @Test
  fun shouldDeleteRecipeFromFirebaseAndSetSavedRecipeToFalseAndUpdateIconAndShowSnackBarWhenFavoriteButtonClickedIfRecipeSaved() {
    givenRecipe(MONDAY)
    givenSavedRecipe(true)
    whenFavoriteButtonClicked(true, isSavedRecipe, mockRecipe)
    thenDeleteRecipeFromFirebase(Times(1), mockRecipe)
    thenSetFavoriteButtonIcon(Times(1), R.drawable.ic_favorite_border_red_24dp)
    thenShowSnackBar(Times(1), R.string.snackbar_recipe_deleted)
  }

  @Test
  fun shouldSaveRecipeToFirebaseAndSetSavedRecipeToTrueAndUpdateIconAndShowSnackBarWhenFavoriteButtonClickedIfRecipeNotSaved() {
    givenRecipe(MONDAY)
    givenSavedRecipe(false)
    whenFavoriteButtonClicked(true, isSavedRecipe, mockRecipe)
    thenSaveUserIdAndEmail(Times(1))
    thenSaveRecipeToFirebase(mockRecipe)
    thenIsSavedRecipe(true)
    thenSetFavoriteButtonIcon(Times(1), R.drawable.ic_favorite_red_24dp)
    thenShowSnackBar(Times(1), R.string.snackbar_recipe_saved)
  }

  @Test
  fun shouldLaunchUIAuthActivityWhenLoggedOutWhenMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.MONDAY.name)
    givenRecipe(valueOf(selectedDay))
    givenAddedToMealPlanAlready(true)
    givenSavedRecipe(true)
    whenOnMealPlanDialogPositiveButtonClickedCalled(false, mockRecipe, view.addedToMealPlan,
        selectedDay,
        isSavedRecipe)
    theLaunchUiAuthActivity(Times(1))
    thenUpdateMealPlanRecipeDayCalled(never())
    thenSaveRecipeToMealPlanDb(never())
    thenSetMealPlanButtonText(never(), selectedDay)
  }

  @Test
  fun shouldUpdateMealPlanRecipeDayIfAlreadyAddedToMealPlanDbWhenMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.MONDAY.name)
    givenRecipe(valueOf(selectedDay))
    givenAddedToMealPlanAlready(true)
    givenSavedRecipe(true)
    whenOnMealPlanDialogPositiveButtonClickedCalled(true, mockRecipe, view.addedToMealPlan,
        selectedDay,
        isSavedRecipe)
    thenSaveUserIdAndEmail(Times(1))
    thenUpdateMealPlanRecipeDayCalled(Times(1))
  }

  @Test
  fun shouldSaveRecipeToMealPlanDbAndSetAddedToMealPlanTrueIfNotAlreadyAddedToMealPlanDbWhenMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.FRIDAY.name)
    givenRecipe(valueOf(selectedDay))
    givenAddedToMealPlanAlready(false)
    givenSavedRecipe(true)
    whenOnMealPlanDialogPositiveButtonClickedCalled(true, mockRecipe, view.addedToMealPlan,
        selectedDay,
        isSavedRecipe)
    thenSaveUserIdAndEmail(Times(1))
    thenSaveRecipeToMealPlanDb(Times(1))
  }

  @Test
  fun shouldSetMealPlanButtonTextToDayIfNotRemovedWhenOnMealPlanDialogPositiveButtonClicked() {
    givenDay(FRIDAY.name)
    givenRecipe(valueOf(selectedDay))
    whenOnMealPlanDialogPositiveButtonClickedCalled(true, mockRecipe, view.addedToMealPlan,
        selectedDay, isSavedRecipe)
    thenSetMealPlanButtonText(Times(1), selectedDay)
    thenRemoveRecipeFromMealPlan(never(), mockRecipe)
    thenSetMealPlanButtonTextToDefault(never(), R.string.detail_meal_plan_button_text)
  }

  @Test
  fun shouldSetMealPlanButtonTextToDefaultIfRemovedWhenOnMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.REMOVE.name)
    givenRecipe(valueOf(selectedDay))
    whenOnMealPlanDialogPositiveButtonClickedCalled(true, mockRecipe, view.addedToMealPlan,
        selectedDay, isSavedRecipe)
    thenSetMealPlanButtonTextToDefault(Times(1), R.string.detail_meal_plan_button_text)
    thenRemoveRecipeFromMealPlan(Times(1), mockRecipe)
    thenSetMealPlanButtonText(never(), selectedDay)
  }

  @Test
  fun shouldShowRecipeRemovedTextWhenRemoveSelectedFromMealPlanWhenShowRecipeDetailSnackBarCalled() {
    givenDay(REMOVE.name)
    givenRecipe(valueOf(selectedDay))
    whenShowRecipeDetailSnackBarCalled(selectedDay)
    thenShowSnackBar(Times(1), R.string.detail_snackbar_meal_plan_removed)
  }

  @Test
  fun shouldShowRecipeSavedToDayOfWeekTextWhenRemoveNotSelectedFromMealPlanWhenShowRecipeDetailSnackBarCalled() {
    givenDay(MONDAY.name)
    givenRecipe(valueOf(selectedDay))
    whenShowRecipeDetailSnackBarCalled(selectedDay)
    thenShowSnackBar(Times(1), R.string.detail_meal_plan_snackbar_text, selectedDay)
  }

  @Test
  fun shouldDisplayDayOfWeekOnMealPlanButtonWhenRecipeAddedToMealPlanAndDayOfWeekNotEmptyWhenSetUpMealPlanButtonTextCalled() {
    givenDay(TUESDAY.name)
    givenRecipe(valueOf(selectedDay), true)
    whenSetUpMealPlanButtonTextCalled()
    thenSetMealPlanButtonText(Times(1), selectedDay)
  }

  @Test
  fun shouldNotDisplayDayOfWeekOnMealPlanButtonWhenRecipeNotAddedToMealPlanOrDayOfWeekEmptyWhenSetUpMealPlanButtonTextCalled() {
    givenDay(TUESDAY.name)
    givenRecipe(valueOf(selectedDay), false)
    whenSetUpMealPlanButtonTextCalled()
    thenSetMealPlanButtonText(never(), selectedDay)
  }

  private fun givenSavedRecipe(isSaved: Boolean) {
    isSavedRecipe = isSaved
  }

  private fun givenRecipe(day: DayOfWeek, mealPlan: Boolean = false) {
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
        mealPlan = mealPlan
    )
  }

  private fun givenAddedToMealPlanAlready(added: Boolean) {
    whenever(view.addedToMealPlan).thenReturn(added)
  }

  private fun givenDay(dayOfWeek: String) {
    selectedDay = dayOfWeek
  }

  private fun whenFavoriteButtonClicked(isSignedIn: Boolean, savedRecipe: Boolean,
      recipeToSave: Recipe) {
    presenter.onFavoriteButtonClicked(isSignedIn, savedRecipe, recipeToSave)
  }

  private fun whenSetSavedRecipeIconCalled(savedRecipe: Boolean) {
    presenter.setSavedRecipeIcon(savedRecipe)
  }

  private fun whenOnMealPlanDialogPositiveButtonClickedCalled(isSignedIn: Boolean,
      mockRecipe: Recipe,
      addedToMealPlan: Boolean, selectedDay: String,
      savedRecipe: Boolean) {
    presenter.onMealPlanDialogPositiveButtonClicked(isSignedIn, mockRecipe, addedToMealPlan,
        selectedDay,
        savedRecipe)
  }

  private fun whenShowRecipeDetailSnackBarCalled(dayOfWeek: String) {
    presenter.showRecipeDetailSnackBar(dayOfWeek)
  }

  private fun whenSetUpMealPlanButtonTextCalled() {
    presenter.setUpMealPlanButtonText(mockRecipe)
  }

  private fun thenSetFavoriteButtonIcon(times: VerificationMode, icon: Int) {
    verify(view, times).setFavoritedButtonFrame(icon)
  }

  private fun thenSaveRecipeToMealPlanDb(times: VerificationMode) {
    verify(firebaseRecipeRepository, times).saveRecipeToMealPlan(mockRecipe,
        DayOfWeek.valueOf(selectedDay), isSavedRecipe)
  }

  private fun thenUpdateMealPlanRecipeDayCalled(times: VerificationMode) {
    verify(firebaseRecipeRepository, times).updateMealPlanRecipeDay(mockRecipe,
        DayOfWeek.valueOf(selectedDay))
  }

  private fun thenIsSavedRecipe(isSaved: Boolean) {
    verify(view).isSavedRecipe = isSaved
  }

  private fun thenSaveRecipeToFirebase(recipeToSave: Recipe) {
    verify(firebaseRecipeRepository).saveRecipeAsFavorite(recipeToSave)
  }

  private fun thenDeleteRecipeFromFirebase(times: VerificationMode, recipeToDelete: Recipe) {
    verify(firebaseRecipeRepository, times).removeRecipeAsFavorite(recipeToDelete)
  }

  private fun thenShowSnackBar(times: VerificationMode, snackBarString: Int,
      dayOfWeek: String? = "") {
    verify(view, times).showRecipeDetailSnackBar(snackBarStringRes = snackBarString,
        dayOfWeek = dayOfWeek)
  }

  private fun thenSetMealPlanButtonTextToDefault(times: VerificationMode, defaultStringRes: Int) {
    verify(view, times).setMealPlanButtonText(mealPlanButtonStringRes = defaultStringRes)
  }

  private fun thenSetMealPlanButtonText(times: VerificationMode, selectedDay: String?) {
    verify(view, times).setMealPlanButtonText(selectedDayString = selectedDay)
  }

  private fun thenRemoveRecipeFromMealPlan(times: VerificationMode, recipe: Recipe) {
    verify(firebaseRecipeRepository, times).removeRecipeFromMealPlan(recipe)
  }

  private fun thenSaveUserIdAndEmail(times: VerificationMode) {
    verify(firebaseRecipeRepository, times).saveUserIdAndUserEmail()
  }
}