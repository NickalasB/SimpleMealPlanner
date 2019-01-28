package com.zonkey.simplemealplanner.model.edamam

data class Recipe(
    val uri: String,
    val label: String,
    val image: String,
    val source: String,
    val yield: Int,
    val calories: Float,
    val totalWeight: Float,
    val ingredients: List<Ingredient>,
    val totalNutrients: NutrientInfo,
    val totalDaily: NutrientInfo,
    val dietLabels: List<Diet>)