package com.zonkey.simplemealplanner.network

import com.zonkey.simplemealplanner.model.RecipePreview
import io.reactivex.Observable

class RecipePuppyService constructor(private val apiClient: GetRecipeApiClient) : RecipeService {

    override fun searchRecipesByKeyWord(searchParams: String, page: Int): Observable<List<RecipePreview>> {
        return apiClient.searchRecipes(searchParameters = searchParams, page = page)
            .map { getRecipeResponse -> getRecipeResponse.getRecipes() }
    }

    override fun searchRecipesByIngredient(ingredients: String): Observable<List<RecipePreview>> {
        return apiClient.searchRecipes(ingredients = ingredients)
            .map { getRecipeResponse -> getRecipeResponse.getRecipes() }
    }
}