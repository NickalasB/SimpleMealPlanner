package com.zonkey.simplemealplanner.activity

import android.view.View
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.zonkey.simplemealplanner.model.DayOfWeek.FRIDAY
import com.zonkey.simplemealplanner.model.DayOfWeek.MONDAY
import com.zonkey.simplemealplanner.model.Diet
import com.zonkey.simplemealplanner.model.Ingredient
import com.zonkey.simplemealplanner.model.NutrientInfo
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.network.RecipeRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import org.mockito.verification.VerificationMode


@RunWith(JUnit4::class)
class MainActivityPresenterTest {

  @Mock
  private lateinit var view: MainView

  @Mock
  private lateinit var recipeRepository: RecipeRepository

  private lateinit var presenter: MainActivityPresenter

  private lateinit var dbRecipes: List<Recipe>

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    presenter = MainActivityPresenter(view, recipeRepository)
  }

  @Test
  fun shouldHideFavoritesTitleIfDbRecipesAreEmpty() {
    givenEmptyDbRecipes()
    whenSetFavoriteRecipesCalled(dbRecipes)
    thenSetFavoritesTitleVisibility(Times(1), View.GONE)
    thenSetEmptySearchViewVisibility(never(), View.GONE)
    thenSetFavoritesTitleVisibility(never(), View.VISIBLE)
    thenSetFavoritedRecipes(Times(1), emptyList())
  }

  @Test
  fun shouldHideEmptySearchViewAndShowFavoriteTitleAndSetRecipeSavedToTrueAndSetFavoritedRecipesIfDbRecipesAreNotNullWhenSetFavoriteRecipesCalled() {
    givenDbRecipes()
    whenSetFavoriteRecipesCalled(dbRecipes)
    thenSetEmptySearchViewVisibility(Times(1), View.GONE)
    thenSetFavoritesTitleVisibility(Times(1), View.VISIBLE)
    thenSetFavoritedRecipes(Times(1), dbRecipes)
    thenSetEmptySearchViewVisibility(never(), View.VISIBLE)
  }

  @Test
  fun shouldHideMealPlanTitleIfDbRecipesAreEmpty() {
    givenEmptyDbRecipes()
    whenSetMealPlanRecipesCalled(dbRecipes)
    thenSetMealPlanTitleVisibility(Times(1), View.GONE)
    thenSetMealPlanRecipes(Times(1), dbRecipes)
    thenSetEmptySearchViewVisibility(never(), View.VISIBLE)
  }

  @Test
  fun shouldHideEmptySearchViewAndShowMealPlanTitleWhenSetMealPlanRecipesCalled() {
    givenDbRecipes()
    whenSetMealPlanRecipesCalled(dbRecipes)
    thenSetEmptySearchViewVisibility(Times(1), View.GONE)
    thenSetMealPlanTitleVisibility(Times(1), View.VISIBLE)
    thenSetMealPlanRecipes(Times(1), dbRecipes)
  }

  @Test
  fun shouldSmoothScrollToNewestMealPlanRecipeIfCurrentMealPlanCountIsGreaterThanSavedCount() {
    whenScrollToNewMealPlanMealIfAddedCalled(currentMealPlanCount = 10, savedMealPlanCount = 9)
    thenSmoothScrollToMealPlanRecipeAtPosition(Times(1), 10)
  }

  @Test
  fun shouldNeverSmoothScrollIfCurrentMealPlanCountIsNotGreaterThanSavedCount() {
    whenScrollToNewMealPlanMealIfAddedCalled(currentMealPlanCount = 10, savedMealPlanCount = 10)
    thenSmoothScrollToMealPlanRecipeAtPosition(never(), 10)
  }

  @Test
  fun shouldSmoothScrollToNewestFavoriteRecipeIfCurrentFavoriteCountIsGreaterThanSavedCount() {
    whenScrollToNewFavoriteMealIfAddedCalled(currentFavoritesCount = 10, savedFavoritesCount = 9)
    thenSmoothScrollToFavoriteRecipeAtPosition(Times(1), 10)
  }

  @Test
  fun shouldNeverSmoothScrollIfCurrentFavoriteCountIsNotGreaterThanSavedCount() {
    whenScrollToNewFavoriteMealIfAddedCalled(currentFavoritesCount = 10, savedFavoritesCount = 10)
    thenSmoothScrollToFavoriteRecipeAtPosition(never(), 10)
  }

  @Test
  fun shouldRefreshSavedRecipeViewsAndSaveHasRefreshedStateIfLoggedInAndHasNotRefreshedSavedRecipesWhenRefreshSavedRecipesCalled() {
    whenRefreshSavedRecipesCalled(loggedIn = true, hasRefreshed = false)
    thenRefreshSavedRecipeViews(Times(1))
    thenSaveHasRefreshedToSharedPrefs(Times(1))
  }

  @Test
  fun shouldNotRefreshSavedRecipeViewsAndSaveHasRefreshedStateIfNotLoggedInAndHasNotRefreshedSavedRecipesWhenRefreshSavedRecipesCalled() {
    whenRefreshSavedRecipesCalled(loggedIn = false, hasRefreshed = false)
    thenRefreshSavedRecipeViews(never())
    thenSaveHasRefreshedToSharedPrefs(never())
  }

  @Test
  fun shouldNotRefreshSavedRecipeViewsAndSaveHasRefreshedStateIfLoggedInAndHasRefreshedSavedRecipesWhenRefreshSavedRecipesCalled() {
    whenRefreshSavedRecipesCalled(loggedIn = true, hasRefreshed = true)
    thenRefreshSavedRecipeViews(never())
    thenSaveHasRefreshedToSharedPrefs(never())
  }

  private fun givenDbRecipes() {
    dbRecipes = listOf(
        Recipe(
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
            day = MONDAY,
            favorite = true,
            mealPlan = false
        ),
        Recipe(
            uri = "testUri2",
            label = "testLabel2",
            image = "testImage2",
            source = "testSource2",
            url = "testUrl2",
            `yield` = 0f,
            dietLabels = emptyList<Diet>(),
            healthLabels = emptyList<Diet>(),
            ingredientLines = emptyList<String>(),
            ingredients = emptyList<Ingredient>(),
            calories = 0f,
            totalWeight = 0f,
            totalNutrients = NutrientInfo(),
            totalDaily = NutrientInfo(),
            key = "testKey2",
            day = FRIDAY,
            favorite = false,
            mealPlan = true
        )
    )
  }

  private fun givenEmptyDbRecipes() {
    dbRecipes = emptyList()
  }


  private fun whenSetFavoriteRecipesCalled(dbRecipes: List<Recipe>) {
    presenter.setFavoriteRecipes(dbRecipes)
  }

  private fun whenSetMealPlanRecipesCalled(dbRecipes: List<Recipe>) {
    presenter.setMealPlanRecipes(dbRecipes)
  }

  private fun whenScrollToNewFavoriteMealIfAddedCalled(currentFavoritesCount: Int,
      savedFavoritesCount: Int) {
    presenter.scrollToNewFavoriteMealIfAdded(currentFavoritesCount, savedFavoritesCount)
  }

  private fun whenScrollToNewMealPlanMealIfAddedCalled(currentMealPlanCount: Int,
      savedMealPlanCount: Int) {
    presenter.scrollToNewMealPlanMealIfAdded(currentMealPlanCount, savedMealPlanCount)
  }

  private fun whenRefreshSavedRecipesCalled(loggedIn: Boolean, hasRefreshed: Boolean) {
    presenter.refreshSavedRecipes(loggedIn, hasRefreshed)
  }

  private fun thenSetEmptySearchViewVisibility(times: VerificationMode, visibility: Int) {
    verify(view, times).setEmptySearchViewVisibility(visibility)
  }

  private fun thenSetFavoritesTitleVisibility(times: VerificationMode, visibility: Int) {
    verify(view, times).setFavoritesTitleVisibility(visibility)
  }

  private fun thenSetFavoritedRecipes(times: VerificationMode, dbRecipes: List<Recipe>) {
    verify(view, times).setFavoritedRecipes(dbRecipes)
  }

  private fun thenSetMealPlanRecipes(times: VerificationMode, dbRecipes: List<Recipe>) {
    verify(view, times).setMealPlanRecipes(dbRecipes)
  }

  private fun thenSetMealPlanTitleVisibility(times: VerificationMode, visibility: Int) {
    verify(view, times).setMealPlanTitleVisibility(visibility)
  }

  private fun thenSmoothScrollToFavoriteRecipeAtPosition(times: VerificationMode,
      recipePosition: Int) {
    verify(view, times).smoothScrollToNewestFavoritesRecipe(recipePosition)
  }

  private fun thenSmoothScrollToMealPlanRecipeAtPosition(times: VerificationMode,
      recipePosition: Int) {
    verify(view, times).smoothScrollToNewestMealPlanRecipe(recipePosition)
  }

  private fun thenRefreshSavedRecipeViews(times: VerificationMode) {
    verify(view, times).refreshSavedRecipeViews()
  }

  private fun thenSaveHasRefreshedToSharedPrefs(times: VerificationMode) {
    verify(view, times).saveHasRefreshedToSharedPrefs()
  }
}