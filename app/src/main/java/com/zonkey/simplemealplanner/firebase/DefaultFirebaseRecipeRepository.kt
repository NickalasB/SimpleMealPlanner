package com.zonkey.simplemealplanner.firebase

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe
import timber.log.Timber
import javax.inject.Inject

const val MEAL_PLANNER_DB_REF = "simple_meal_planner"
const val USERS = "users"
const val EMAIL = "email"
const val USER_ID = "userId"
const val RECIPES = "recipes"
const val DAY = "day"
const val MEAL_PLAN = "mealPlan"
const val FAVORITE = "favorite"

class DefaultFirebaseRecipeRepository @Inject constructor(
    private val firebaseDbInstance: FirebaseDatabase,
    private val firebaseAuthRepository: FirebaseAuthRepository) : FirebaseRecipeRepository {


  override val usersReference: DatabaseReference
    get() = firebaseDbInstance.getReference(MEAL_PLANNER_DB_REF)
        .child(USERS)

  override val userRecipeDatabase: DatabaseReference
    get() = firebaseDbInstance.getReference(MEAL_PLANNER_DB_REF)
        .child(USERS)
        .child(firebaseAuthRepository.currentUser?.uid.toString())
        .child(RECIPES)

  override fun saveRecipeAsFavorite(recipe: Recipe) {

    if ((userRecipeDatabase.child(recipe.key).key != recipe.key)) {
      val key = userRecipeDatabase.push().key ?: ""
      recipe.key = key
      userRecipeDatabase.child(recipe.key).setValue(recipe)
      userRecipeDatabase.child(recipe.key).child(FAVORITE).setValue(true)
    } else {
      userRecipeDatabase
          .child(recipe.key)
          .child(FAVORITE)
          .setValue(true)
    }
  }

  override fun removeRecipeAsFavorite(recipe: Recipe) {
    userRecipeDatabase
        .child(recipe.key)
        .child(FAVORITE)
        .setValue(false)
  }

  override fun saveRecipeToMealPlan(recipe: Recipe, dayOfWeek: DayOfWeek,
      isSavedRecipe: Boolean): Task<Void> {
    val favoriteRecipeDbRef = userRecipeDatabase
    return if ((favoriteRecipeDbRef.child(recipe.key).key != recipe.key)) {
      val key = favoriteRecipeDbRef.push().key ?: ""
      recipe.key = key
      Tasks.whenAll(
          favoriteRecipeDbRef.child(recipe.key).setValue(recipe),
          favoriteRecipeDbRef.child(recipe.key).child(DAY).setValue(dayOfWeek),
          favoriteRecipeDbRef.child(recipe.key).child(MEAL_PLAN).setValue(true)
      )
    } else {
      Tasks.whenAll(
          favoriteRecipeDbRef.child(recipe.key).child(DAY).setValue(dayOfWeek),
          favoriteRecipeDbRef.child(recipe.key).child(MEAL_PLAN).setValue(true)
      )
    }
  }

  override
  fun saveRecipeToSharedDB(userId: String, recipe: Recipe, dayOfWeek: DayOfWeek): Task<Void> {
    val favoriteRecipeDbRef =
        firebaseDbInstance.getReference(MEAL_PLANNER_DB_REF)
            .child(USERS)
            .child(userId)
            .child(RECIPES)
    val key = favoriteRecipeDbRef.push().key ?: ""
    recipe.key = key
    return Tasks.whenAll(
        favoriteRecipeDbRef.child(recipe.key).setValue(recipe),
        favoriteRecipeDbRef.child(recipe.key).child(DAY).setValue(dayOfWeek),
        favoriteRecipeDbRef.child(recipe.key).child(MEAL_PLAN).setValue(true)
    )
  }

  override fun updateMealPlanRecipeDay(recipe: Recipe, dayOfWeek: DayOfWeek) {
    userRecipeDatabase
        .child(recipe.key)
        .child(DAY)
        .setValue(dayOfWeek)
  }

  override fun removeRecipeFromMealPlan(recipe: Recipe): Task<Void> {
    val recipeDbRef = userRecipeDatabase
        .child(recipe.key)
    return recipeDbRef.child(MEAL_PLAN).setValue(false)
  }

  override fun saveUserIdAndUserEmail() {
    val userId = firebaseAuthRepository.currentUser?.uid.toString()
    val singleUserReference = firebaseDbInstance.getReference(MEAL_PLANNER_DB_REF)
        .child(USERS)
        .child(userId)

    singleUserReference
        .child(USER_ID)
        .setValue(userId)

    singleUserReference
        .child(EMAIL)
        .setValue(firebaseAuthRepository.currentUser?.email)
  }

  override fun purgeUnsavedRecipe(recipe: Recipe) {
    userRecipeDatabase
        .child(recipe.key)
        .addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Problem deleting recipe ${recipe.label} from Firebase")
          }

          override fun onDataChange(snapshot: DataSnapshot) {
            if (!recipe.key.isEmpty()) {
              val dbRecipe = snapshot.getValue(Recipe::class.java)
              dbRecipe?.let {
                if (!it.favorite && !it.mealPlan) {
                  snapshot.ref.removeValue()
                }
              }
            }
          }
        })
  }
}