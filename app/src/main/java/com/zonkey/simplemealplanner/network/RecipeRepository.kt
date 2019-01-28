package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Hits
import io.reactivex.Observable

interface RecipeRepository {

  fun getEdamamHits(): Observable<Hits>

  fun getEdamamRecipes(): Observable<List<Hit>>

//  fun getRecipePuppyRecipesByKeyword(keyWords: String,
//      page: Int = 1): Observable<List<RecipePreview>>
//
//  fun getRecipePuppyRecipesByIngredient(ingredients: String): Observable<List<RecipePreview>>
}