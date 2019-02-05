package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.edamam.Hit
import io.reactivex.Observable
import javax.inject.Inject

class DefaultRecipeRepository @Inject constructor(
    private val recipeService: RecipeService,
    private val firebaseRecipeRepository: FirebaseRecipeRepository) : RecipeRepository {

  override fun getEdamamHits(queryText: String): Observable<List<Hit>> {
    if (firebaseRecipeRepository.getRecipes().isEmpty()) {
      return getRecipeHits(queryText).doOnNext {
        firebaseRecipeRepository.createRecipes(it)
      }
    } else {
      return Observable.just(firebaseRecipeRepository.getRecipes())
    }
  }

  private fun getRecipeHits(queryText: String): Observable<List<Hit>> {
    return recipeService.getEdamamHitsQuery(queryText).map { it.hits }
  }

}