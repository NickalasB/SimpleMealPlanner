package com.zonkey.simplemealplanner.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.iid.InstanceIdResult

interface FirebaseInstanceIdRepository {

  fun getToken(): Task<InstanceIdResult>
}