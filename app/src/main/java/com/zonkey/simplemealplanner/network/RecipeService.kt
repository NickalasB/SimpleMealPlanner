package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.edamam.Hits
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

//const val RECIPE_PUPPY_BASE_URL = "http://www.recipepuppy.com/"
const val EDAMAM_BASE_URL = "https://api.edamam.com/"

interface RecipeService {

  //info https://developer.edamam.com/edamam-docs-recipe-api

  //example "https://api.edamam.com/search?q=chicken&app_id=${YOUR_APP_ID}&app_key=${YOUR_APP_KEY}&from=0&to=3&calories=591-722&health=alcohol-free"

  @GET("search")
  fun getEdamamRecipesRequest(
      @Query("q") queryText: String = "Pork Chops",
      @Query("app_id") appId: String = "3763ab0e",
      @Query("app_key") appKey: String = "769489cdf6326639c81bfe5f3e54d491",
      @Query("from") from: Int = 0,
//      @Query("calories") calories: String = "591-722",
      @Query("to") to: Int = 20): Observable<Hits>
//  @Query("diet") diet: Enum<Diet> = Diet.HIGH_PROTEIN)


  //info http://www.recipepuppy.com/about/api/
//
//  @GET("api/")
//  fun getRecipePuppyRecipesRequest(
//      @Query("i") ingredients: String = "",
//      @Query("q") searchParameters: String = "",
//      @Query("p") page: Int? = null): Observable<Hits>
}