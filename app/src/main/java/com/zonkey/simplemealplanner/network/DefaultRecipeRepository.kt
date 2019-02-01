package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Hits
import io.reactivex.Observable
import javax.inject.Inject

class DefaultRecipeRepository @Inject constructor(
    private val recipeService: RecipeService) : RecipeRepository {

  private var cachedRecipeHits = emptyList<Hit>()

  override fun getEdamamHits(queryText: String): Observable<Hits> {
    if (cachedRecipeHits.isEmpty()) {
      return recipeService.getEdamamHitsQuery(queryText = queryText)
          .doOnNext { cachedRecipeHits = it.hits }
    } else {
      return Observable.just(cachedRecipeHits)
          .concatMap { recipeService.getEdamamHitsQuery(queryText = queryText) }
          .doOnNext { cachedRecipeHits = it.hits }
    }
  }
}