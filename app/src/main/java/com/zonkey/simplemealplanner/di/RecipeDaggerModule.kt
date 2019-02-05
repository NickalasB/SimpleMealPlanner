package com.zonkey.simplemealplanner.di

import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.network.DefaultRecipeRepository
import com.zonkey.simplemealplanner.network.RecipeRepository
import com.zonkey.simplemealplanner.network.RecipeService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RecipeDaggerModule {

  @Provides
  @Singleton
  fun provideRecipeService(retrofit: Retrofit): RecipeService =
      retrofit.create(RecipeService::class.java)

  @Provides
  @Singleton
  fun provideRecipeRepository(
      recipeService: RecipeService,
      firebaseRecipeRepository: FirebaseRecipeRepository): RecipeRepository =
      DefaultRecipeRepository(recipeService, firebaseRecipeRepository)
}