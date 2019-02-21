package com.zonkey.simplemealplanner.activity

import android.view.View
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.network.RecipeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainActivityPresenter(
    private val view: MainView,
    private val recipeRepository: RecipeRepository) {

  fun getRecipeHits(queryText: String, compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(
        recipeRepository.getEdamamHits(queryText = queryText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { recipeHits ->

              view.setQueryTitleText(queryText)
              view.setUpAdapter(recipeHits)

              if (recipeHits.isEmpty()) {
                view.setEmptySearchViewVisibility(View.VISIBLE)
              }

//              val recipeJson = Gson().toJson(recipeHits)
            }
            .doOnSubscribe {
              view.setHomePageProgressVisibility(View.VISIBLE)
              view.setEmptySearchViewVisibility(View.GONE)
            }
            .doOnComplete { view.setHomePageProgressVisibility(View.GONE) }
            .doOnError { error ->
              view.setHomePageProgressVisibility(View.GONE)
              view.setEmptySearchViewVisibility(View.VISIBLE)
              Timber.e(error, "Bad Search")
            }
            .subscribe()
    )
  }

  fun onDestroy(compositeDisposable: CompositeDisposable) {
    if (!compositeDisposable.isDisposed) {
      compositeDisposable.dispose()
    }
  }

  fun setFavoriteRecipes(dbRecipes: List<Recipe?>?) {
    dbRecipes?.let {
      if (it.isNullOrEmpty()) {
        view.setFavoritesTitleVisibility(View.GONE)
        view.setFavoritedRecipes(dbRecipes)
      } else {
        view.setEmptySearchViewVisibility(View.GONE)
        view.setFavoritesTitleVisibility(View.VISIBLE)
        view.setFavoritedRecipes(dbRecipes)
      }
    }
  }

  fun setMealPlanRecipes(mealPlanRecipes: List<Recipe?>?) {
    mealPlanRecipes?.let {
      if (it.isNullOrEmpty()) {
        view.setMealPlanTitleVisibility(View.GONE)
        view.setMealPlanRecipes(mealPlanRecipes)
      } else {
        view.setEmptySearchViewVisibility(View.GONE)
        view.setMealPlanTitleVisibility(View.VISIBLE)
        view.setMealPlanRecipes(mealPlanRecipes)

      }
    }
  }
}