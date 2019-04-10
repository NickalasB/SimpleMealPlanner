package com.zonkey.simplemealplanner.activity

import androidx.annotation.DrawableRes
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

  private val selectedAnimation = 1f
  private val unselectedAnimation = -1f

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)

    presenter = RecipeDetailActivityPresenter(view, firebaseRecipeRepository)
  }

  @Test
  fun shouldSetFavoriteIconToOutlinedHeartWhenRecipeNotSaved() {
    givenSavedRecipe(false)
    whenSetSavedRecipeIconCalled(savedRecipe = isSavedRecipe)
    thenSetFavoriteButtonIcon(Times(1), unselectedAnimation)
  }

  @Test
  fun shouldSetFavoriteIconToFilledInHeartWhenRecipeIsSaved() {
    givenSavedRecipe(true)
    whenSetSavedRecipeIconCalled(savedRecipe = isSavedRecipe)
    thenSetFavoriteButtonIcon(Times(1), selectedAnimation)
  }

  @Test
  fun shouldLaunchUIAuthActivityWhenNotLoggedInWhenFavoriteButtonClicked() {
    givenRecipe(MONDAY)
    givenSavedRecipe(true)
    whenFavoriteButtonClicked(false, isSavedRecipe, mockRecipe)
    thenLaunchUiAuthActivity(Times(1))
    thenDeleteRecipeFromFirebase(never(), mockRecipe)
    thenSetFavoriteButtonIcon(never(), unselectedAnimation)
    thenShowSnackBar(never(), R.string.snackbar_recipe_deleted)
  }

  @Test
  fun shouldDeleteRecipeFromFirebaseAndSetSavedRecipeToFalseAndUpdateIconAndShowSnackBarWhenFavoriteButtonClickedIfRecipeSaved() {
    givenRecipe(MONDAY)
    givenSavedRecipe(true)
    whenFavoriteButtonClicked(true, isSavedRecipe, mockRecipe)
    thenDeleteRecipeFromFirebase(Times(1), mockRecipe)
    thenSetFavoriteButtonIcon(Times(1), unselectedAnimation)
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
    thenSetFavoriteButtonIcon(Times(1), selectedAnimation)
    thenShowSnackBar(Times(1), R.string.snackbar_recipe_saved)
  }

  @Test
  fun shouldLaunchUIAuthActivityWhenLoggedOutWhenMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.MONDAY.name)
    givenRecipe(valueOf(selectedDay))
    givenAddedToMealPlan(true)
    givenSavedRecipe(true)
    whenOnMealPlanDialogPositiveButtonClickedCalled(false, mockRecipe, view.addedToMealPlan,
        selectedDay,
        isSavedRecipe)
    thenLaunchUiAuthActivity(Times(1))
    thenUpdateMealPlanRecipeDayCalled(never())
    thenSaveRecipeToMealPlanDb(never())
    thenSetMealPlanButtonText(never(), selectedDay)
  }

  @Test
  fun shouldUpdateMealPlanRecipeDayIfAlreadyAddedToMealPlanDbWhenMealPlanDialogPositiveButtonClicked() {
    givenDay(DayOfWeek.MONDAY.name)
    givenRecipe(valueOf(selectedDay))
    givenAddedToMealPlan(true)
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
    givenAddedToMealPlan(mockRecipe.mealPlan)
    whenOnMealPlanDialogPositiveButtonClickedCalled(true, mockRecipe, view.addedToMealPlan,
        selectedDay, isSavedRecipe)
    thenSetMealPlanButtonTextToDefault(Times(1), R.string.detail_meal_plan_button_text)
    thenRemoveRecipeFromMealPlan(Times(1), mockRecipe)
    thenSetMealPlanButtonText(never(), selectedDay)
    thenRecipeAddedToMealPlan(false)
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
    givenRecipe(valueOf(selectedDay), mealPlan = true)
    whenSetUpMealPlanButtonTextCalled()
    thenSetMealPlanButtonText(Times(1), selectedDay)
  }

  @Test
  fun shouldNotDisplayDayOfWeekOnMealPlanButtonWhenRecipeNotAddedToMealPlanOrDayOfWeekEmptyWhenSetUpMealPlanButtonTextCalled() {
    givenDay(TUESDAY.name)
    givenRecipe(valueOf(selectedDay), mealPlan = false)
    whenSetUpMealPlanButtonTextCalled()
    thenSetMealPlanButtonText(never(), selectedDay)
  }

  @Test
  fun shouldAnimateSavedRecipeIconToSelectedIfRecipeIsSavedWhenSetUpFavoriteButtonCalled() {
    givenSavedRecipe(true)
    whenSetUpFavoriteButtonCalled(firstTimeInActivity = false)
    thenSetFavoriteButtonIcon(Times(1), selectedAnimation)
    thenShowFavoriteButtonTutorialCircle(never())
    thenSetIsFirstTimeInActivity(never(), false)
  }

  @Test
  fun shouldNotAnimateFavoriteButtonIfRecipeIsNotAlreadySavedWhenSetUpFavoriteButtonCalled() {
    givenSavedRecipe(false)
    whenSetUpFavoriteButtonCalled(firstTimeInActivity = false)
    thenSetFavoriteButtonIcon(never(), unselectedAnimation)
  }

  @Test
  fun shouldShowFavoriteButtonTutorialAndSetIsFirstTimeInActivityToFalseWhenRecipeNotSavedWhenSetUpFavoriteButtonCalled() {
    givenSavedRecipe(false)
    whenSetUpFavoriteButtonCalled(firstTimeInActivity = true)
    thenShowFavoriteButtonTutorialCircle(Times(1))
    thenSetIsFirstTimeInActivity(Times(1), false)
  }

  @Test
  fun shouldShowShareButtonTutorialIfSavedRecipeAndHasNotSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, favorite = true)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = false)
    thenShowShareButtonTutorialCircle(Times(1))
  }

  @Test
  fun shouldShowShareButtonTutorialIfMealPlanRecipeAndHasNotSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, mealPlan = true)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = false)
    thenShowShareButtonTutorialCircle(Times(1))
  }

  @Test
  fun shouldNotShowShareButtonTutorialIfNotSavedRecipeAndHasNotSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, favorite = false)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = false)
    thenShowShareButtonTutorialCircle(never())
  }

  @Test
  fun shouldNotShowShareButtonTutorialIfNotMealPlanRecipeOrFavoriteAndHasNotSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, favorite = false, mealPlan = false)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = false)
    thenShowShareButtonTutorialCircle(never())
  }

  @Test
  fun shouldNotShowShareButtonTutorialIfSavedRecipeAndHasSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, favorite = true)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = true)
    thenShowShareButtonTutorialCircle(never())
  }

  @Test
  fun shouldNotShowShareButtonTutorialIfMealPlanRecipeeAndHasSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, mealPlan = true)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = true)
    thenShowShareButtonTutorialCircle(never())
  }

  @Test
  fun shouldNotShowShareButtonTutorialIfNotSavedRecipeAndHasSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, favorite = false)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = true)
    thenShowShareButtonTutorialCircle(never())
  }

  @Test
  fun shouldNotShowShareButtonTutorialIfNotMealPlanRecipeAndHasSeenTutorialWhenShowShareButtonTutorialCalled() {
    givenRecipe(MONDAY, mealPlan = false)
    whenShowShareButtonTutorialCalled(recipe = mockRecipe, hasSeenTutorial = true)
    thenShowShareButtonTutorialCircle(never())
  }

  @Test
  fun shouldSetShareButtonEnabledBackgroundIfIsMealPlanRecipeWhenSetShareButtonBackgroundCalled() {
    givenRecipe(TUESDAY, mealPlan = true)
    givenAddedToMealPlan(mockRecipe.mealPlan)
    whenSetShareButtonBackgroundCalled(mockRecipe)
    thenSetShareButtonBackground(Times(1), R.drawable.ic_share_index_blue_24dp)
  }

  @Test
  fun shouldSethareButtonDisabledBackgroundIfIsMealPlanRecipeWhenSetShareButtonBackgroundCalled() {
    givenRecipe(TUESDAY, mealPlan = false)
    givenAddedToMealPlan(mockRecipe.mealPlan)
    whenSetShareButtonBackgroundCalled(mockRecipe)
    thenSetShareButtonBackground(Times(1), R.drawable.ic_share_disabled_24dp)
  }

  @Test
  fun shouldNotShowShareButtonIfNotMealPlanRecipeOrFavoriteRecipeWhenSetupShareButtonVisibilityCalled() {
    givenRecipe(TUESDAY, favorite = false, mealPlan = false)
    givenAddedToMealPlan(mockRecipe.mealPlan)
    whenSetShareButtonBackgroundCalled(mockRecipe)
    thenSetShareButtonBackground(Times(1), R.drawable.ic_share_disabled_24dp)
  }

  @Test
  fun shouldHandlePermissionRequestIfPermissionNotGrantedWhenOnShareButtonClickedCalled() {
    givenAddedToMealPlan(true)
    whenOnShareButtonClickedCalled(
        permissionGranted = false,
        savedToMealPlanAlready = view.addedToMealPlan)
    thenHandlePermissionRequest(Times(1))
    thenLaunchContactPicker(never())
    thenShowSnackBar(never(), R.string.disabled_share_button_snackbar_message)
  }

  @Test
  fun shouldLaunchContactPickerIfPermissionGrantedWhenOnShareButtonClickedCalled() {
    givenAddedToMealPlan(true)
    whenOnShareButtonClickedCalled(
        permissionGranted = true,
        savedToMealPlanAlready = view.addedToMealPlan)
    thenLaunchContactPicker(Times(1))
    thenHandlePermissionRequest(never())
    thenShowSnackBar(never(), R.string.disabled_share_button_snackbar_message)
  }

  @Test
  fun shouldShowShareButtonDisabledSnackBarWhenOnShareButtonClickedIfNotSavedToMealPlanAready() {
    givenAddedToMealPlan(false)
    whenOnShareButtonClickedCalled(
        permissionGranted = false,
        savedToMealPlanAlready = view.addedToMealPlan
    )
    thenShowSnackBar(Times(1), R.string.disabled_share_button_snackbar_message)
    thenLaunchContactPicker(never())
    thenHandlePermissionRequest(never())
  }

  private fun givenRecipe(day: DayOfWeek, mealPlan: Boolean = false, favorite: Boolean = false) {
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
        favorite = favorite,
        mealPlan = mealPlan
    )
  }

  private fun givenAddedToMealPlan(added: Boolean) {
    whenever(view.addedToMealPlan).thenReturn(added)
  }

  private fun givenDay(dayOfWeek: String) {
    selectedDay = dayOfWeek
  }

  private fun givenSavedRecipe(isSaved: Boolean) {
    isSavedRecipe = isSaved
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
    presenter.setupMealPlanButtonText(mockRecipe)
  }

  private fun whenSetUpFavoriteButtonCalled(firstTimeInActivity: Boolean) {
    presenter.setUpFavoriteButton(isSavedRecipe, firstTimeInActivity)
  }

  private fun whenShowShareButtonTutorialCalled(recipe: Recipe, hasSeenTutorial: Boolean) {
    presenter.showShareButtonTutorial(recipe.favorite || recipe.mealPlan, hasSeenTutorial)
  }

  private fun whenSetShareButtonBackgroundCalled(recipe: Recipe) {
    presenter.setShareButtonBackground(recipe.mealPlan)
  }

  private fun whenOnShareButtonClickedCalled(permissionGranted: Boolean,
      savedToMealPlanAlready: Boolean) {
    presenter.onShareButtonClicked(permissionGranted, savedToMealPlanAlready)
  }

  private fun thenSetFavoriteButtonIcon(times: VerificationMode, speed: Float) {
    verify(view, times).setFavoritedButtonAnimationDirection(speed)
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

  private fun thenShowFavoriteButtonTutorialCircle(times: VerificationMode) {
    verify(view, times).showFavoriteButtonAndMealPlanButtonTutorialCircles()
  }

  private fun thenSetIsFirstTimeInActivity(times: VerificationMode, firstTime: Boolean) {
    verify(view, times).setIsFirstTimeInActivity(firstTime)
  }

  private fun thenShowShareButtonTutorialCircle(times: VerificationMode) {
    verify(view, times).showShareButtonTutorialCircle()
  }

  private fun thenHandlePermissionRequest(times: VerificationMode) {
    verify(view, times).handlePermissionRequest()
  }

  private fun thenLaunchContactPicker(times: VerificationMode) {
    verify(view, times).launchContactPicker()
  }

  private fun thenSetShareButtonBackground(
      times: VerificationMode, @DrawableRes buttonBackgroundId: Int) {
    verify(view, times).setShareButtonBackground(buttonBackgroundId)
  }

  private fun thenRecipeAddedToMealPlan(addedToMealPlan: Boolean) {
    when (addedToMealPlan) {
      true -> assertTrue("addedToMealPlan expected to be true", view.addedToMealPlan)
      false -> assertFalse("addedToMealPlan expected to be false", view.addedToMealPlan)
    }
  }

  private fun thenLaunchUiAuthActivity(times: Times) {
    verify(view, times).launchUIAuthActivity()
  }
}