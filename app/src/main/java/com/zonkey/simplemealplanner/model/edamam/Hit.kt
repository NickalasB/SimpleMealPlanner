package com.zonkey.simplemealplanner.model.edamam

data class Hit(
    val recipe: Recipe) {

  fun getHit() = this

  fun getEdamamRecipe() = recipe

}