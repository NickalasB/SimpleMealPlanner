package com.zonkey.simplemealplanner.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zonkey.simplemealplanner.model.Recipe
import timber.log.Timber
import javax.inject.Inject

const val RECIPE_DB_INSTANCE = "simple_meal_planner"
const val RECIPE_DB = "recipe_db"

class DefaultFirebaseRecipeRepository @Inject constructor(
    private val firebaseDbInstance: FirebaseDatabase) : FirebaseRecipeRepository {

  override fun saveRecipeToFirebase(recipe: Recipe) {
    val key = firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(RECIPE_DB).push().key
        ?: ""
    recipe.key = key
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(RECIPE_DB).child(key).setValue(recipe)
  }

  override fun deleteRecipeFromFirebase(recipe: Recipe) {
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(RECIPE_DB).child(recipe.key)
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