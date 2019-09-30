package com.zonkey.simplemealplanner.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek.MONDAY
import com.zonkey.simplemealplanner.model.Diet
import com.zonkey.simplemealplanner.model.Ingredient
import com.zonkey.simplemealplanner.model.NutrientInfo
import com.zonkey.simplemealplanner.model.Recipe
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_create_recipe.create_recipe_recipe_title
import kotlinx.android.synthetic.main.activity_create_recipe.detailed_recipe_card_view
import kotlinx.android.synthetic.main.create_recipe_detail_view.create_recipe_save_button
import javax.inject.Inject

class CreateRecipeActivity : AppCompatActivity() {


  @Inject
  lateinit var firebaseRepo: FirebaseRecipeRepository

  private lateinit var createdRecipe: Recipe


  private lateinit var presenter: CreateRecipePresenter

  companion object {
    fun buildIntent(context: Context): Intent = Intent(context, CreateRecipeActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_recipe)

    presenter = CreateRecipePresenter(firebaseRepo)

    createdRecipe = Recipe(
        uri = "testUri1",
        label = create_recipe_recipe_title.text.toString(),
        image = "testImage1",
        source = "testSource1",
        url = "testUrl1",
        `yield` = 0f,
        dietLabels = emptyList<Diet>(),
        healthLabels = emptyList<Diet>(),
        ingredientLines = emptyList<String>(),
        ingredients = emptyList<Ingredient>(),
        calories = 0f,
        totalWeight = 0f,
        totalNutrients = NutrientInfo(),
        totalDaily = NutrientInfo(),
        key = "testKey1",
        day = MONDAY,
        favorite = true,
        mealPlan = false,
        fromShare = false,
        sharedFromUser = ""
    )



    create_recipe_save_button.setOnClickListener {

      detailed_recipe_card_view.addNewRecipeLine()


//      presenter.saveRecipe(createdRecipe)
    }
  }

}
