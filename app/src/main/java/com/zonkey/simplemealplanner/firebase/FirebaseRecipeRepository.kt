package com.zonkey.simplemealplanner.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe

interface FirebaseRecipeRepository {

  val usersReference: DatabaseReference

  val userRecipeDatabase: DatabaseReference

  fun saveRecipeToSharedDB(userId: String, recipe: Recipe, dayOfWeek: DayOfWeek): Task<Task<Void>>

  fun saveUserIdAndUserEmail(): Task<Task<Void>>

  fun saveRecipeAsFavorite(recipe: Recipe): Task<Task<Void>>

  fun removeRecipeAsFavorite(recipe: Recipe): Task<Void>

  fun saveRecipeToMealPlan(recipe: Recipe, dayOfWeek: DayOfWeek, isSavedRecipe: Boolean): Task<Task<Void>>

  fun purgeUnsavedRecipe(recipe: Recipe)

  fun updateMealPlanRecipeDay(recipe: Recipe, dayOfWeek: DayOfWeek): Task<Void>

  fun removeRecipeFromMealPlan(recipe: Recipe): Task<Void>
}