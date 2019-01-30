package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Hits
import io.reactivex.Observable

interface RecipeRepository {

  fun getEdamamHits(queryText: String): Observable<Hits>

  fun getEdamamRecipes(queryText: String): Observable<List<Hit>>

//  fun getRecipePuppyRecipesByKeyword(keyWords: String,
//      page: Int = 1): Observable<List<RecipePreview>>
//
//  fun getRecipePuppyRecipesByIngredient(ingredients: String): Observable<List<RecipePreview>>
}