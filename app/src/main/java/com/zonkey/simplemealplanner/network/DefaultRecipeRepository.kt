package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.Hit
import io.reactivex.Observable
import javax.inject.Inject

class DefaultRecipeRepository @Inject constructor(
    private val recipeService: RecipeService) : RecipeRepository {

  override fun getEdamamHits(queryText: String): Observable<List<Hit>> {
    return try {
      recipeService.getEdamamHitsQuery(queryText).map { it.hits }
    } catch (e: NetworkConnectivityException) {
      Observable.just(emptyList())
    }
  }
}