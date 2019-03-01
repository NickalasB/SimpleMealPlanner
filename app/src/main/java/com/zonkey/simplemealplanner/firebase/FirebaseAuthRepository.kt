package com.zonkey.simplemealplanner.firebase

import android.content.Intent
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepository {

  val currentUser: FirebaseUser?

  val authProviders: ArrayList<IdpConfig>

  fun authActivityIntent(): Intent

}