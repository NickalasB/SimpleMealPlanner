package com.zonkey.simplemealplanner.activity

import android.view.View
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
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
    theSetFavoritesTitleVisibility(Times(1), View.GONE)
    thenSetEmptySearchViewVisibility(never(), View.GONE)
    theSetFavoritesTitleVisibility(never(), View.VISIBLE)
    thenSetIsSavedRecipeCard(never(), true)
    thenSetFavoritedRecipes(never(), dbRecipes)
  }

  private fun givenEmptyDbRecipes() {
    dbRecipes = emptyList()
  }

  @Test
  fun shouldHideEmptySearchViewAndShowFavoriteTitleAndSetRecipeSavedToTrueAndSetFavoritedRecipesIfDbRecipesAreNotNullWhenSetFavoriteRecipesCalled() {
    givenDbRecipes()
    whenSetFavoriteRecipesCalled(dbRecipes)
    thenSetEmptySearchViewVisibility(Times(1), View.GONE)
    theSetFavoritesTitleVisibility(Times(1), View.VISIBLE)
    thenSetIsSavedRecipeCard(Times(1), true)
    thenSetFavoritedRecipes(Times(1), dbRecipes)
    thenSetEmptySearchViewVisibility(never(), View.VISIBLE)
  }

  private fun thenSetEmptySearchViewVisibility(times: VerificationMode, visibility: Int) {
    verify(view, times).setEmptySearchViewVisibility(visibility)
  }

  private fun theSetFavoritesTitleVisibility(times: VerificationMode, visibility: Int) {
    verify(view, times).setFavoritesTitleVisibility(visibility)
  }

  private fun thenSetIsSavedRecipeCard(times: VerificationMode, saved: Boolean) {
    verify(view, times).setIsSavedRecipeCard(saved)
  }

  private fun thenSetFavoritedRecipes(times: VerificationMode, dbRecipes: List<Recipe>) {
    verify(view, times).setFavoritedRecipes(dbRecipes)
  }

  private fun whenSetFavoriteRecipesCalled(dbRecipes: List<Recipe>) {
    presenter.setFavoriteRecipes(dbRecipes)
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
            key = "testKey1"
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
            key = "testKey2"
        )
    )
  }

}