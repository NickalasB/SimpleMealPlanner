package com.zonkey.simplemealplanner.firebase

import com.zonkey.simplemealplanner.model.Recipe

interface FirebaseRecipeRepository {

  fun saveRecipeToFirebase(recipe: Recipe)

  fun deleteRecipeFromFirebase(recipe: Recipe)

}