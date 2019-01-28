package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Hits
import io.reactivex.Observable
import javax.inject.Inject

class DefaultRecipeRepository @Inject constructor(
    private val recipeService: RecipeService) : RecipeRepository {

  override fun getEdamamRecipes(): Observable<List<Hit>> {
    return recipeService.getEdamamRecipesRequest().map { hits -> hits.getHitList() }
  }

  override fun getEdamamHits(): Observable<Hits> {
    return recipeService.getEdamamRecipesRequest().map { hits -> hits.getHit() }
  }

//  override fun getRecipePuppyRecipesByKeyword(keyWords: String,
//      page: Int): Observable<List<RecipePreview>> {
//    return recipeService.getRecipePuppyRecipesRequest(searchParameters = keyWords, page = page)
//        .map { getRecipeResponse -> getRecipeResponse.getRecipes() }
//  }
//
//  override fun getRecipePuppyRecipesByIngredient(
//      ingredients: String): Observable<List<RecipePreview>> {
//    return recipeService.getRecipePuppyRecipesRequest(ingredients = ingredients)
//        .map { getRecipeResponse -> getRecipeResponse.getRecipes() }
//  }

}