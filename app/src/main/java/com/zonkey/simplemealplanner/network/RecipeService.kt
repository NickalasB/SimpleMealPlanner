package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.BuildConfig
import com.zonkey.simplemealplanner.model.Hits
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

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
  fun getEdamamHitsQuery(
      @Query("q") queryText: String,
      @Query("app_id") appId: String = BuildConfig.EDAMAM_APP_ID,
      @Query("app_key") appKey: String = BuildConfig.EDAMAM_APP_KEY,
      @Query("from") from: Int = 0,
      @Query("to") to: Int = 21,
      @Query("ingr") maxIngredients: Int? = 99,
      @Query("calories") calories: String? = "1-9000"): Observable<Hits>
}