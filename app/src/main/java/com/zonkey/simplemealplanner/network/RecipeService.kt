package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.BuildConfig
import com.zonkey.simplemealplanner.model.edamam.Hits
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

//const val RECIPE_PUPPY_BASE_URL = "http://www.recipepuppy.com/"
const val EDAMAM_BASE_URL = "https://api.edamam.com/"

interface RecipeService {

  //info https://developer.edamam.com/edamam-docs-recipe-api
  //example "https://api.edamam.com/search?q=chicken&app_id=${YOUR_APP_ID}&app_key=${YOUR_APP_KEY}&from=0&to=3&calories=591-722&health=alcohol-free"

  /**
   * The makeup of a recipe search request
   * @param queryText the search term/s
   * @param appId your edamam-issued application ID
   * @param appKey your edamam-issued application key
   * @param from first result index (default 0)
   * @param to last result index
   * @param maxIngredients maxium number of ingredients
   * @param calories a range of total calories formatted as a string "500-600"
   * will return all recipes with which have between 500 and 600 kcal per serving
   */
  @GET("search")
  fun getEdamamRecipesRequest(
      @Query("q") queryText: String,
      @Query("app_id") appId: String = BuildConfig.edamam_app_id,
      @Query("app_key") appKey: String = BuildConfig.edamam_app_key,
      @Query("from") from: Int = 0,
      @Query("to") to: Int = 20,
      @Query("ingr") maxIngredients: Int? = 99,
      @Query("calories") calories: String? = "1-9000"): Observable<Hits>

  //info http://www.recipepuppy.com/about/api/
//
//  @GET("api/")
//  fun getRecipePuppyRecipesRequest(
//      @Query("i") ingredients: String = "",
//      @Query("q") searchParameters: String = "",
//      @Query("p") page: Int? = null): Observable<Hits>
}