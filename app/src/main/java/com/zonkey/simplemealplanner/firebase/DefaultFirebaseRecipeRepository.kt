package com.zonkey.simplemealplanner.firebase

import com.google.firebase.database.FirebaseDatabase
import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Recipe
import javax.inject.Inject

const val RECIPE_DB_INSTANCE = "simple_meal_planner"
const val RECIPE_DB = "recipe_db"

class DefaultFirebaseRecipeRepository @Inject constructor(private val firebaseDbInstance: FirebaseDatabase): FirebaseRecipeRepository {



  override fun getRecipes() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createRecipes(recipeHits: List<Hit>) {

    val newRecipes = mutableListOf<Recipe>()
    recipeHits.forEach { hit: Hit -> newRecipes.add(hit.recipe) }
    firebaseDbInstance.getReference(RECIPE_DB_INSTANCE).child(RECIPE_DB).setValue(newRecipes)

  }

  override fun updateRecipes(query: String) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}