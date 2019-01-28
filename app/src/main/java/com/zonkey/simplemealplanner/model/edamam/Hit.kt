package com.zonkey.simplemealplanner.model.edamam

data class Hit(
    val recipe: Recipe,
    val bookmarked: Boolean,
    val bought: Boolean
) {

  fun getHit() = this

  fun getEdamamRecipe() = recipe

}