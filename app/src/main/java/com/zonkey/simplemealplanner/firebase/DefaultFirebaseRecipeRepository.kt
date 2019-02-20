package com.zonkey.simplemealplanner.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe
import timber.log.Timber
import javax.inject.Inject

const val RECIPE_DB_INSTANCE = "simple_meal_planner"
const val FAVORITE_RECIPE_DB = "favorite_recipe_db"
const val MEAL_PLAN_DB = "meal_plan_db"
const val DAY = "day"

class DefaultFirebaseRecipeRepository @Inject constructor(
    private val firebaseDbInstance: FirebaseDatabase) : FirebaseRecipeRepository {

  override fun saveRecipeToFavoritesDb(recipe: Recipe) {
    val favoriteRecipeDbRef = firebaseDbInstance.getReference(RECIPE_DB_INSTANCE)
        .child(FAVORITE_RECIPE_DB)

    val key = favoriteRecipeDbRef.push().key ?: ""
    recipe.key = key
    favoriteRecipeDbRef.child(key).setValue(recipe)
  }

  override fun deleteRecipeFromFavoritesDb(recipe: Recipe) {
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(FAVORITE_RECIPE_DB)
        .child(recipe.key)
        .addValueEventListener(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Problem deleting recipe ${recipe.label} from Firebase")
          }

          override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.ref.removeValue()
          }
        })
  }

  override fun saveRecipeToMealPlanDb(recipe: Recipe, dayOfWeek: DayOfWeek) {
    val mealPlanDbRef = firebaseDbInstance.getReference(RECIPE_DB_INSTANCE)
        .child(MEAL_PLAN_DB)

    if (mealPlanDbRef.child(recipe.key).key != recipe.key) {
      val key = mealPlanDbRef.push().key ?: ""
      recipe.key = key
      mealPlanDbRef.child(recipe.key).setValue(recipe)
      mealPlanDbRef.child(recipe.key).child(DAY).ref.setValue(dayOfWeek)
    } else {
      return
    }
  }

  override fun deleteRecipeFromMealPlanDb(recipe: Recipe) {
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(MEAL_PLAN_DB)
        .child(recipe.key)
        .addValueEventListener(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Problem deleting recipe ${recipe.label} from MealPlan")
          }

          override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.ref.removeValue()
          }
        })
  }

  override fun updateMealPlanRecipeDay(recipe: Recipe, dayOfWeek: DayOfWeek) {
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE)
        .child(MEAL_PLAN_DB)
        .child(recipe.key)
        .child(DAY)
        .setValue(dayOfWeek)
  }
}