package com.zonkey.simplemealplanner.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.R.string
import com.zonkey.simplemealplanner.firebase.FirebaseAuthRepository
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.Hit
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.network.RecipeRepository
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.favorites_recipe_card_widget
import kotlinx.android.synthetic.main.activity_main.home_page_progress
import kotlinx.android.synthetic.main.activity_main.main_search_recipe_card_widget
import kotlinx.android.synthetic.main.activity_main.meal_plan_recipe_card_widget
import kotlinx.android.synthetic.main.activity_main.recipe_card_favorites_title
import kotlinx.android.synthetic.main.activity_main.recipe_card_meal_plan_title
import kotlinx.android.synthetic.main.activity_main.recipe_card_query_title
import kotlinx.android.synthetic.main.activity_main.recipe_empty_search_view
import kotlinx.android.synthetic.main.activity_main.recipe_main_constraint_layout
import kotlinx.android.synthetic.main.activity_main.recipe_search_view
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainView {

  companion object {
    private const val RC_SIGN_IN_MAIN = 200
  }

  @Inject
  lateinit var recipeRepository: RecipeRepository

  @Inject
  lateinit var firebaseRecipeRepository: FirebaseRecipeRepository

  @Inject
  lateinit var authUI: AuthUI

  @Inject
  lateinit var firebaseAuthRepository: FirebaseAuthRepository

  private val compositeDisposable = CompositeDisposable()

  private lateinit var presenter: MainActivityPresenter

  private var signedIn = false

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    recipe_search_view.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    recipe_search_view.setIconifiedByDefault(false)
    recipe_search_view.isSubmitButtonEnabled = true

    presenter = MainActivityPresenter(this, recipeRepository)

    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

    handleSearchQuery()
  }

  private fun handleSearchQuery() {
    if (Intent.ACTION_SEARCH == intent.action) {
      intent.getStringExtra(SearchManager.QUERY)?.also {
        if (it.isNotEmpty()) {
          presenter.getRecipeHits(it, compositeDisposable)
        }
      }
    } else {
      recipe_empty_search_view.visibility = View.VISIBLE
      recipe_empty_search_view.text = getString(string.recipe_empty_start_text)
    }
  }

  override fun onResume() {
    super.onResume()

    signedIn = firebaseAuthRepository.currentUser != null

    setUpFavoriteRecipes()

    setUpMealPlanRecipes()
  }

  private fun setUpFavoriteRecipes() {
    firebaseRecipeRepository.userRecipeDatabase
        .addValueEventListener(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Problem retrieving favorite recipes from database")
            recipe_empty_search_view.visibility = View.VISIBLE
          }

          override fun onDataChange(snapshot: DataSnapshot) {
            val dbRecipes: List<Recipe?>? = snapshot.children.map {
              it.getValue(Recipe::class.java)
            }

            presenter.setFavoriteRecipes(dbRecipes?.filter { it?.favorite == true })
          }
        })
  }

  private fun setUpMealPlanRecipes() {
    firebaseRecipeRepository.userRecipeDatabase
        .addValueEventListener(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) {
            Timber.e(error.toException(), "Problem retrieving meal plan recipes from database")
            recipe_empty_search_view.visibility = View.VISIBLE
          }

          override fun onDataChange(snapshot: DataSnapshot) {
            val dbRecipes: List<Recipe?>? = snapshot.children.map {
              it.getValue(Recipe::class.java)
            }

            presenter.setMealPlanRecipes(dbRecipes?.filter { it?.mealPlan == true })
          }
        })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.main_menu, menu)
    return true
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    super.onPrepareOptionsMenu(menu)
    return when (signedIn) {
      true -> {
        menu.removeItem(R.id.sign_in)
        if (menu.findItem(R.id.sign_out) == null) {
          menu.add(Menu.NONE, R.id.sign_out, Menu.NONE, R.string.menu_sign_out)
        }
        true
      }
      false -> {
        menu.removeItem(R.id.sign_out)
        if (menu.findItem(R.id.sign_in) == null) {
          menu.add(Menu.NONE, R.id.sign_in, Menu.NONE, R.string.menu_sign_in)
        }
        true
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.sign_out -> {
        authUI.signOut(this).addOnCompleteListener {
          Snackbar.make(recipe_main_constraint_layout,
              getString(string.snack_bar_sign_out_message), Snackbar.LENGTH_SHORT).show()
          recipe_search_view.clearFocus()
          signedIn = false
        }
        true
      }
      R.id.sign_in -> {
        startActivityForResult(firebaseAuthRepository.authActivityIntent(), RC_SIGN_IN_MAIN)
        true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == RC_SIGN_IN_MAIN) {
      val response = IdpResponse.fromResultIntent(data)

      if (resultCode == Activity.RESULT_OK) {
        Snackbar.make(recipe_main_constraint_layout, getString(string.snackbar_sign_in_message),
            Snackbar.LENGTH_SHORT).show()
        firebaseRecipeRepository.saveUserEmail()

      } else {
        Timber.e(response?.error, "Failed to log-in")
      }

    }
  }

  override fun setUpAdapter(recipeHits: List<Hit>) {
    val recipes = recipeHits.map { it.recipe }
    main_search_recipe_card_widget.setRecipes(recipes)
  }

  override fun setEmptySearchViewVisibility(visibility: Int) {
    recipe_empty_search_view.visibility = visibility
  }

  override fun setQueryTitleText(queryText: String) {
    recipe_card_query_title.text = queryText.split(' ').joinToString(" ") { it.capitalize() }
  }

  override fun setHomePageProgressVisibility(visibility: Int) {
    home_page_progress.visibility = visibility
  }

  override fun setFavoritedRecipes(dbRecipes: List<Recipe?>) {
    favorites_recipe_card_widget.setRecipes(dbRecipes)
  }

  override fun setMealPlanTitleVisibility(visibility: Int) {
    recipe_card_meal_plan_title.visibility = visibility
  }

  override fun setMealPlanRecipes(mealPlanRecipes: List<Recipe?>) {
    meal_plan_recipe_card_widget.setRecipes(mealPlanRecipes)
  }

  override fun setFavoritesTitleVisibility(visibility: Int) {
    recipe_card_favorites_title.visibility = visibility
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.onDestroy(compositeDisposable)
  }
}
