package com.zonkey.simplemealplanner.model

data class RecipeResponse(
    val title: String,
    val version: String,
    val results: List<RecipePreview>
) {
  fun getRecipes() = results
}

