package com.zonkey.simplemealplanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zonkey.simplemealplanner.network.GetRecipeApiClient
import com.zonkey.simplemealplanner.network.RecipePuppyService
import com.zonkey.simplemealplanner.network.RecipeService
import com.zonkey.simplemealplanner.network.RetrofitInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var recipeApiClient: GetRecipeApiClient
    private lateinit var recipeService: RecipeService
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeApiClient()

        recipeService = RecipePuppyService(recipeApiClient)

        compositeDisposable.add(
            recipeService.searchRecipesByIngredient("cheese, ham")
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
        recipeApiClient = RetrofitInstance().getRetrofitInstance().create(GetRecipeApiClient::class.java)

    }
}
