package com.zonkey.simplemealplanner.firebase

import com.zonkey.simplemealplanner.model.Hit

interface FirebaseRecipeRepository {

  fun getRecipes(): List<Hit>

  fun createRecipes(recipeHits: List<Hit>)

  fun updateRecipes(query: String)

}