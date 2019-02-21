package com.zonkey.simplemealplanner.model

import com.zonkey.simplemealplanner.model.DayOfWeek.MONDAY

data class Recipe(
    val uri: String,
    val label: String,
    val image: String,
    val source: String,
    val url: String,
    val yield: Float,
    val dietLabels: List<Diet>,
    val healthLabels: List<Diet>,
    val ingredientLines: List<String>,
    val ingredients: List<Ingredient>,
    val calories: Float,
    val totalWeight: Float,
    val totalNutrients: NutrientInfo,
    val totalDaily: NutrientInfo,
    var key: String,
    var day: DayOfWeek,
    var favorite: Boolean,
    var mealPlan: Boolean
) {

  constructor() : this(
      uri = "",
      label = "",
      image = "",
      source = "",
      url = "",
      `yield` = 0f,
      dietLabels = emptyList<Diet>(),
      healthLabels = emptyList<Diet>(),
      ingredientLines = emptyList<String>(),
      ingredients = emptyList<Ingredient>(),
      calories = 0f,
      totalWeight = 0f,
      totalNutrients = NutrientInfo(),
      totalDaily = NutrientInfo(),
      key = "",
      day = MONDAY,
      favorite = false,
      mealPlan = false
  )

}