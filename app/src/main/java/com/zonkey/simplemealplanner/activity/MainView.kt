package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.model.Hit
import com.zonkey.simplemealplanner.model.Recipe

interface MainView {

  fun setQueryTitleText(queryText: String)

  fun setEmptySearchViewVisibility(visibility: Int)

  fun setHomePageProgressVisibility(visibility: Int)

  fun setUpAdapter(recipeHits: List<Hit>)

  fun setFavoritesTitleVisibility(visibility: Int)

  fun setIsSavedRecipeCard(isSaved: Boolean)

  fun setFavoritedRecipes(dbRecipes: List<Recipe?>)
}