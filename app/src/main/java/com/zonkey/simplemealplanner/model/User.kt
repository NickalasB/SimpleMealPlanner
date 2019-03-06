package com.zonkey.simplemealplanner.model

class User(
    var userId: String = "",
    val email: String = "",
    var recipes: HashMap<String, Recipe?> = hashMapOf()
)
