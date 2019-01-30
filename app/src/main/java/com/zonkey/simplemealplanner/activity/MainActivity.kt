package com.zonkey.simplemealplanner.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.adapter.RecipeCardAdapter
import com.zonkey.simplemealplanner.network.RecipeRepository
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.home_page_progress
import kotlinx.android.synthetic.main.activity_main.recipe_search_view
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var recipeRepository: RecipeRepository

  private val compositeDisposable = CompositeDisposable()
  private lateinit var recyclerView: RecyclerView
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    recipe_search_view.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    recipe_search_view.setIconifiedByDefault(false)
    recipe_search_view.isSubmitButtonEnabled = true

    if (Intent.ACTION_SEARCH == intent.action) {
      intent.getStringExtra(SearchManager.QUERY)?.also {
        if (it.isNotEmpty()) {
          getTestRecipes(it)
        }
      }
    } else {
      getTestRecipes("Salmon")
    }
  }

  private fun getTestRecipes(queryText: String) {

    compositeDisposable.add(
        recipeRepository.getEdamamRecipes(queryText = queryText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { recipeList ->
              viewManager = LinearLayoutManager(this)
              viewAdapter = RecipeCardAdapter(recipeList)

              recyclerView = findViewById<RecyclerView>(R.id.recipe_card_recycler_view).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
              }
              val recipeJson = Gson().toJson(recipeList)
            }
            .doOnSubscribe { home_page_progress.visibility = View.VISIBLE }
            .doOnComplete {
              home_page_progress.visibility = View.GONE
            }
            .doOnError { e ->
              //ToDo proper error handling
              home_page_progress.visibility = View.GONE
            }
            .subscribe()
    )
  }
}
