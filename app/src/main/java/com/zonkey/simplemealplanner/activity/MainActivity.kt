package com.zonkey.simplemealplanner.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.R.id
import com.zonkey.simplemealplanner.R.string
import com.zonkey.simplemealplanner.adapter.RecipeCardAdapter
import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Recipe
import com.zonkey.simplemealplanner.network.RecipeRepository
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.home_page_progress
import kotlinx.android.synthetic.main.activity_main.recipe_empty_search_view
import kotlinx.android.synthetic.main.activity_main.recipe_search_view
import timber.log.Timber
import javax.inject.Inject

private const val FIREBASE_INSTANCE = "simple_meal_planner"
private const val RECIPE_DB = "recipe_db"

class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var recipeRepository: RecipeRepository

  private val compositeDisposable = CompositeDisposable()
  private lateinit var recyclerView: RecyclerView
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager

  private lateinit var recipeDatabase: DatabaseReference
  private lateinit var firebaseInstance: FirebaseDatabase

  private var recipeHitsId = ""

  private lateinit var sharedPreferences: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    recipe_search_view.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    recipe_search_view.setIconifiedByDefault(false)
    recipe_search_view.isSubmitButtonEnabled = true

    sharedPreferences = getSharedPreferences("TEST_PREFS", Context.MODE_PRIVATE)
    recipeHitsId = sharedPreferences.getString("SHARED_PREF_KEY", "") ?: ""


    firebaseInstance = FirebaseDatabase.getInstance()
    recipeDatabase = firebaseInstance.getReference(RECIPE_DB)

    handleSearchQuery()
  }

  private fun handleSearchQuery() {
    if (Intent.ACTION_SEARCH == intent.action) {
      intent.getStringExtra(SearchManager.QUERY)?.also {
        if (it.isNotEmpty()) {
          getTestRecipes(it)
        }
      }
    } else {
      recipe_empty_search_view.visibility = View.VISIBLE
      recipe_empty_search_view.text = getString(string.recipe_empty_start_text)
    }
  }

  private fun getTestRecipes(queryText: String) {
    compositeDisposable.add(
        recipeRepository.getEdamamHits(queryText = queryText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { recipeHits ->

              setUpAdapter(recipeHits.hits)

              if (recipeHits.hits.isEmpty()) {
                displayEmptyResultsView()
              } else {
                if (recipeHitsId.isEmpty()) {
                  createRecipesOnFirebase(recipeHits.hits)
                } else {
                  updateRecipeOnFirebase(recipeHits.hits[0].recipe)
                }
              }

              val recipeJson = Gson().toJson(recipeHits)
            }
            .doOnSubscribe {
              home_page_progress.visibility = View.VISIBLE
              recipe_empty_search_view.visibility = View.GONE
            }
            .doOnComplete { home_page_progress.visibility = View.GONE }
            .doOnError { error ->
              home_page_progress.visibility = View.GONE
              displayEmptyResultsView()
              Timber.e(error, "Bad Search")
            }
            .subscribe()
    )
  }

  private fun createRecipesOnFirebase(recipeHits: List<Hit>) {
//    if (recipeHitsId.isEmpty()) {
//      sharedPreferences.edit().putString("SHARED_PREF_KEY", recipeDatabase.push().key.toString()).apply()
//    }
    val newRecipes = mutableListOf<Recipe>()

    recipeHits.forEach { hit: Hit -> newRecipes.add(hit.recipe) }
    firebaseInstance.getReference(FIREBASE_INSTANCE).child(RECIPE_DB).setValue(newRecipes)
    addRecipeChangeListener()
  }

  private fun addRecipeChangeListener() {
    recipeDatabase.child(recipeHitsId).addValueEventListener(object: ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        val recipe = snapshot.getValue(Recipe()::class.java)

        if(recipe == null) {
          Timber.d("Recipe data is null")
          return
        }

        Timber.d("Recipe changed")

        val newRecipeLabel = recipe.label
      }

      override fun onCancelled(error: DatabaseError) {
        Timber.e(error.toException(), "Failed to read Recipe")
      }
    })
  }

  fun updateRecipeOnFirebase(recipe: Recipe) {
//    recipeDatabase.child(recipeHitsId).child("label").setValue(recipe.label)
//    recipeDatabase.child(recipeHitsId).child("calories").setValue(recipe.calories)
  }

  private fun setUpAdapter(recipeHits: List<Hit>) {
    viewManager = LinearLayoutManager(this)
    viewAdapter = RecipeCardAdapter(recipeHits)

    recyclerView = findViewById<RecyclerView>(id.recipe_card_recycler_view).apply {
      setHasFixedSize(true)
      layoutManager = viewManager
      adapter = viewAdapter
    }
  }

  private fun displayEmptyResultsView() {
    recipe_empty_search_view.visibility = View.VISIBLE
    recipe_empty_search_view.text = getString(string.recipe_empty_error_text)
  }

  override fun onDestroy() {
    super.onDestroy()
    if (!compositeDisposable.isDisposed) {
      compositeDisposable.dispose()
    }
  }
}
