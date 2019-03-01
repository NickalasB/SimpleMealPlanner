package com.zonkey.simplemealplanner.firebase

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class DefaultFirebaseAuthRepository @Inject constructor(
    private val authUI: AuthUI,
    private val firebaseAuth: FirebaseAuth) : FirebaseAuthRepository {

  override val currentUser: FirebaseUser?
    get() = firebaseAuth.currentUser

  override val authProviders: ArrayList<IdpConfig>
    get() = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

  override fun authActivityIntent() =
      authUI
          .createSignInIntentBuilder()
          .setAvailableProviders(authProviders)
          .build()
}