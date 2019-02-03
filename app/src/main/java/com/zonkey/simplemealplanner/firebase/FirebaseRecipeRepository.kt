package com.zonkey.simplemealplanner.firebase

import com.zonkey.simplemealplanner.model.edamam.Hit

interface FirebaseRecipeRepository {

  fun getRecipes()

  fun createRecipes(recipeHits: List<Hit>)

  fun updateRecipes(query: String)

}