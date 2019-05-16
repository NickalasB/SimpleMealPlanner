package com.zonkey.simplemealplanner.activity

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.zonkey.simplemealplanner.model.Hit
import com.zonkey.simplemealplanner.model.Recipe

interface MainView {

  fun setQueryTitleText(queryText: String)

  fun setEmptySearchViewVisibility(visibility: Int)

  fun setSearchErrorMessage(@StringRes messageId: Int)

  fun showErrorAnimation(@RawRes errorAnimation: Int)

  fun setHomePageProgressVisibility(visibility: Int)

  fun setUpAdapter(recipeHits: List<Hit>)

  fun setFavoritesTitleVisibility(visibility: Int)

  fun setFavoritedRecipes(dbRecipes: List<Recipe?>)

  fun setMealPlanTitleVisibility(visibility: Int)

  fun setMealPlanRecipes(mealPlanRecipes: List<Recipe?>)

  fun smoothScrollToNewestMealPlanRecipe(mealPlanCount: Int)

  fun smoothScrollToNewestFavoritesRecipe(favoriteCount: Int)

  fun refreshSavedRecipeViews()

  fun saveHasRefreshedToSharedPrefs()
}