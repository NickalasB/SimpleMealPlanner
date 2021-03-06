package com.zonkey.simplemealplanner.di

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zonkey.simplemealplanner.firebase.DefaultFirebaseAuthRepository
import com.zonkey.simplemealplanner.firebase.DefaultFirebaseInstanceIdRepository
import com.zonkey.simplemealplanner.firebase.DefaultFirebaseRecipeRepository
import com.zonkey.simplemealplanner.firebase.FirebaseAuthRepository
import com.zonkey.simplemealplanner.firebase.FirebaseInstanceIdRepository
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
  fun providesFirebaseInstanceRepository() : FirebaseInstanceIdRepository = DefaultFirebaseInstanceIdRepository()

  @Provides
  @Singleton
  fun provideFirebaseRecipeRepository(
      firebaseInstance: FirebaseDatabase,
      firebaseAuthRepo: FirebaseAuthRepository,
      firebaseInstanceIdRepository: FirebaseInstanceIdRepository): FirebaseRecipeRepository =
      DefaultFirebaseRecipeRepository(firebaseInstance, firebaseAuthRepo, firebaseInstanceIdRepository)

  @Provides
  @Singleton
  fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

  @Provides
  @Singleton
  fun providesFirebaseAuthUi() = AuthUI.getInstance()

  @Provides
  @Singleton
  fun provideFirebaseAuthRepository(authUi: AuthUI, firebaseAuth: FirebaseAuth) : FirebaseAuthRepository =
      DefaultFirebaseAuthRepository(authUi, firebaseAuth)
}
