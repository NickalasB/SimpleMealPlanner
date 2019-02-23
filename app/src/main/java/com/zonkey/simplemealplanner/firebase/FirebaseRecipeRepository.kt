package com.zonkey.simplemealplanner.firebase

import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe

interface FirebaseRecipeRepository {

  fun saveRecipeAsFavorite(recipe: Recipe)

  fun removeRecipeAsFavorite(recipe: Recipe)

  fun saveRecipeToMealPlan(recipe: Recipe, dayOfWeek: DayOfWeek, isSavedRecipe: Boolean)

  fun purgeUnsavedRecipe(recipe: Recipe)

  fun updateMealPlanRecipeDay(recipe: Recipe, dayOfWeek: DayOfWeek)

  fun removeRecipeFromMealPlan(recipe: Recipe)
}