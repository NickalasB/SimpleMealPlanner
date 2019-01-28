package com.zonkey.simplemealplanner

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zonkey.simplemealplanner.adapter.RecipeCardAdapter
import com.zonkey.simplemealplanner.network.RecipeRepository
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.home_page_progress
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

    getTestRecipes()
  }

  private fun getTestRecipes() {

    compositeDisposable.add(
        recipeRepository.getEdamamRecipes()
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

              val recipeJaon = Gson().toJson(recipeList)

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
