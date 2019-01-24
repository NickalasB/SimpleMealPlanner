package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.RecipePreview
import io.reactivex.Observable

interface RecipeRepository {

  fun getRecipesByKeyWord(keyWords: String, page: Int): Observable<List<RecipePreview>>

  fun searchRecipesByIngredient(ingredients: String): Observable<List<RecipePreview>>
}