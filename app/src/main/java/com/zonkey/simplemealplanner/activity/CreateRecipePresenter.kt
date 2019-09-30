package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.Recipe

class CreateRecipePresenter(val firebaseRecipeRepository: FirebaseRecipeRepository) {


  fun saveRecipe(createdRecipe: Recipe) {

    firebaseRecipeRepository.saveRecipeAsFavorite(createdRecipe)

  }
}