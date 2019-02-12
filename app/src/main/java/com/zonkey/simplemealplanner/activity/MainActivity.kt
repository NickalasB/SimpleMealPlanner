package com.zonkey.simplemealplanner.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.R.string
import com.zonkey.simplemealplanner.model.Hit
import com.zonkey.simplemealplanner.network.RecipeRepository
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.home_page_progress
import kotlinx.android.synthetic.main.activity_main.recipe_card_query_title
import kotlinx.android.synthetic.main.activity_main.recipe_empty_search_view
import kotlinx.android.synthetic.main.activity_main.recipe_search_view
import kotlinx.android.synthetic.main.activity_main.test_recipe_card_widget
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainView {

  @Inject
  lateinit var recipeRepository: RecipeRepository

  private val compositeDisposable = CompositeDisposable()

  private lateinit var presenter: MainActivityPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    recipe_search_view.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    recipe_search_view.setIconifiedByDefault(false)
    recipe_search_view.isSubmitButtonEnabled = true

    presenter = MainActivityPresenter(this, recipeRepository)

    handleSearchQuery()
  }

  private fun handleSearchQuery() {
    if (Intent.ACTION_SEARCH == intent.action) {
      intent.getStringExtra(SearchManager.QUERY)?.also {
        if (it.isNotEmpty()) {
          presenter.getTestRecipes(it, compositeDisposable)
        }
      }
    } else {
      recipe_empty_search_view.visibility = View.VISIBLE
      recipe_empty_search_view.text = getString(string.recipe_empty_start_text)
    }
  }

  override fun setUpAdapter(recipeHits: List<Hit>) {
    val recipes = recipeHits.map { it.recipe }
    test_recipe_card_widget.setRecipes(recipes)
  }

  override fun setEmptySearchViewVisibility(visibility: Int) {
    recipe_empty_search_view.visibility = visibility
  }

  override fun setQueryTitleText(queryText: String) {
    recipe_card_query_title.text = queryText
  }

  override fun setHomePageProgressVisibility(visibility: Int) {
    home_page_progress.visibility = visibility
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.onDestroy(compositeDisposable)
  }
}
