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
const val RECIPES_DB = "recipe_db"
const val DAY = "day"
const val MEAL_PLAN = "mealPlan"
const val FAVORITE = "favorite"

class DefaultFirebaseRecipeRepository @Inject constructor(
    private val firebaseDbInstance: FirebaseDatabase) : FirebaseRecipeRepository {

  override fun saveRecipeAsFavorite(recipe: Recipe) {
    val favoriteRecipeDbRef = firebaseDbInstance.getReference(RECIPE_DB_INSTANCE)
        .child(RECIPES_DB)

    if ((favoriteRecipeDbRef.child(recipe.key).key != recipe.key)) {
      val key = favoriteRecipeDbRef.push().key ?: ""
      recipe.key = key
      favoriteRecipeDbRef.child(recipe.key).setValue(recipe)
      favoriteRecipeDbRef.child(recipe.key).child(FAVORITE).setValue(true)
    } else {
      favoriteRecipeDbRef
          .child(recipe.key)
          .child(FAVORITE)
          .setValue(true)
    }
  }

  override fun removeRecipeAsFavorite(recipe: Recipe) {
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE)
        .child(RECIPES_DB)
        .child(recipe.key)
        .child(FAVORITE)
        .setValue(false)
  }

  override fun saveRecipeToMealPlan(recipe: Recipe, dayOfWeek: DayOfWeek,
      isSavedRecipe: Boolean) {
    val favoriteRecipeDbRef = firebaseDbInstance.getReference(RECIPE_DB_INSTANCE)
        .child(RECIPES_DB)
    if ((favoriteRecipeDbRef.child(recipe.key).key != recipe.key)) {
      val key = favoriteRecipeDbRef.push().key ?: ""
      recipe.key = key
      favoriteRecipeDbRef.child(recipe.key).setValue(recipe)
      favoriteRecipeDbRef.child(recipe.key).child(DAY).setValue(dayOfWeek)
      favoriteRecipeDbRef.child(recipe.key).child(MEAL_PLAN).setValue(true)
    } else {
      favoriteRecipeDbRef.child(recipe.key).child(DAY).setValue(dayOfWeek)
      favoriteRecipeDbRef.child(recipe.key).child(MEAL_PLAN).setValue(true)
    }
  }

  override fun updateMealPlanRecipeDay(recipe: Recipe, dayOfWeek: DayOfWeek) {
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE)
        .child(RECIPES_DB)
        .child(recipe.key)
        .child(DAY)
        .setValue(dayOfWeek)
  }

  override fun deleteRecipeFromDb(recipe: Recipe) {
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(RECIPES_DB).child(recipe.key)
        .addValueEventListener(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Problem deleting recipe ${recipe.label} from Firebase")
          }

          override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.ref.removeValue()
          }
        })
  }
}