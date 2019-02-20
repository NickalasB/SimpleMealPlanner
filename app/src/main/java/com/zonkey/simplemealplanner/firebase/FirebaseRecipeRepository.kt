package com.zonkey.simplemealplanner.firebase

import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe

interface FirebaseRecipeRepository {

  fun saveRecipeToFavoritesDb(recipe: Recipe)

  fun deleteRecipeFromFavoritesDb(recipe: Recipe)

  fun saveRecipeToMealPlanDb(recipe: Recipe, dayOfWeek: DayOfWeek)

  fun deleteRecipeFromMealPlanDb(recipe: Recipe)

  fun updateMealPlanRecipeDay(recipe: Recipe, dayOfWeek: DayOfWeek)
}