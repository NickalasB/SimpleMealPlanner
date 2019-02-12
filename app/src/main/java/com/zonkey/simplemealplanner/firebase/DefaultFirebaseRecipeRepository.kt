package com.zonkey.simplemealplanner.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zonkey.simplemealplanner.model.Hit
import timber.log.Timber
import javax.inject.Inject

const val RECIPE_DB_INSTANCE = "simple_meal_planner"
const val RECIPE_DB = "recipe_db"

class DefaultFirebaseRecipeRepository @Inject constructor(
    private val firebaseDbInstance: FirebaseDatabase) : FirebaseRecipeRepository {

  override
  fun getRecipes(): List<Hit> {
    val cachedRecipeHits = mutableListOf<Hit>()
    firebaseDbInstance.getReference(
        RECIPE_DB_INSTANCE).addValueEventListener(object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {

        if (dataSnapshot.hasChild(RECIPE_DB)) {
          dataSnapshot.children.forEach {
            val hit: Hit? = it.getValue(
                Hit::class.java)
            if (hit != null) {
              cachedRecipeHits.add(hit)
            }
          }
        }
      }
      override fun onCancelled(error: DatabaseError) {
        Timber.e(error.toException(), "Problem getting recipes")
      }
    })
    return cachedRecipeHits
  }

  override fun createRecipes(recipeHits: List<Hit>) {
    recipeHits.forEach {
      val key = firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(RECIPE_DB).push().key
          ?: ""
      it.recipe.key = key
      firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(RECIPE_DB).child(key).setValue(it)
    }
  }

  override fun updateRecipes(query: String) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}