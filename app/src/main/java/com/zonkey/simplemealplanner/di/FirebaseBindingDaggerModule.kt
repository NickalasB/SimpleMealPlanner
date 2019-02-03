package com.zonkey.simplemealplanner.di

import com.google.firebase.database.FirebaseDatabase
import com.zonkey.simplemealplanner.firebase.DefaultFirebaseRecipeRepository
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseBindingDaggerModule {

  @Provides
  @Singleton
  fun providesFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

  @Provides
  @Singleton
  fun provideFirebaseRecipeRepository(firebaseInstance: FirebaseDatabase): FirebaseRecipeRepository =
      DefaultFirebaseRecipeRepository(firebaseInstance)
}
