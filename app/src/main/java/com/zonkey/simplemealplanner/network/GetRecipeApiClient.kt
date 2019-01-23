package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.RecipeResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GetRecipeApiClient {

    @GET("api/")
    fun searchRecipes(
        @Query("i") ingredients: String = "",
        @Query("q") searchParameters: String = "",
        @Query("p") page: Int? = null): Observable<RecipeResponse>

}