package com.zonkey.simplemealplanner.model.edamam

data class Ingredient(
    val uri: String,
    val quantity: Float,
    val measure: Measure,
    val weight: Float,
    val food: Food
)