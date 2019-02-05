package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.edamam.Hit
import io.reactivex.Observable

interface RecipeRepository {
  fun getEdamamHits(queryText: String): Observable<List<Hit>>
}