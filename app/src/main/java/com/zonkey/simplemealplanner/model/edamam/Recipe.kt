package com.zonkey.simplemealplanner.model.edamam

data class Recipe(
    val uri: String,
    val label: String,
    val image: String,
    val source: String,
    val url: String,
    val yield: Int,
    val dietLabels: List<Diet>,
    val healthLabels: List<Diet>,
    val ingredientLines: List<String>,
    val ingredients: List<Ingredient>,
    val calories: Float,
    val totalWeight: Float,
    val totalNutrients: NutrientInfo,
    val totalDaily: NutrientInfo
)