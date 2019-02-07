package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.edamam.Hit
import io.reactivex.Observable
import javax.inject.Inject

class DefaultRecipeRepository @Inject constructor(
    private val recipeService: RecipeService) : RecipeRepository {

  override fun getEdamamHits(queryText: String): Observable<List<Hit>> {
    return recipeService.getEdamamHitsQuery(queryText).map { it.hits }
  }
}