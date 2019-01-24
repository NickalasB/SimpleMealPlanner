package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.RecipePreview
import io.reactivex.Observable

class DefaultRecipeRepository constructor(
    private val service: RecipeService) : RecipeRepository {

  override fun getRecipesByKeyWord(keyWords: String, page: Int): Observable<List<RecipePreview>> {
    return service.getRecipesRequest(searchParameters = keyWords, page = page)
        .map { getRecipeResponse -> getRecipeResponse.getRecipes() }
  }

  override fun searchRecipesByIngredient(ingredients: String): Observable<List<RecipePreview>> {
    return service.getRecipesRequest(ingredients = ingredients)
        .map { getRecipeResponse -> getRecipeResponse.getRecipes() }
  }
}