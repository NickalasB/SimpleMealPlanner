package com.zonkey.simplemealplanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zonkey.simplemealplanner.network.RecipeRepository
import com.zonkey.simplemealplanner.network.RecipeService
import com.zonkey.simplemealplanner.network.RetrofitInstance
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  lateinit var recipeService: RecipeService

  @Inject
  lateinit var recipeRepository: RecipeRepository

  private val compositeDisposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    initializeApiClient()

    compositeDisposable.add(
        recipeRepository.searchRecipesByIngredient("cheese, ham")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
              Toast.makeText(this, "onComplete Called!", Toast.LENGTH_SHORT).show()
            }
            .doOnError {
              Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
            .subscribe()
    )

  }

  private fun initializeApiClient() {
    recipeService = RetrofitInstance().getRetrofitInstance().create(
        RecipeService::class.java)

  }
}
