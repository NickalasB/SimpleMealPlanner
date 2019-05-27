package com.zonkey.simplemealplanner.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult

class DefaultFirebaseInstanceIdRepository : FirebaseInstanceIdRepository {

  override fun getToken(): Task<InstanceIdResult> {
    return FirebaseInstanceId.getInstance().instanceId
  }
}