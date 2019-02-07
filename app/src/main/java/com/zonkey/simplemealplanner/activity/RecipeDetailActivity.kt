package com.zonkey.simplemealplanner.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.R.string
import com.zonkey.simplemealplanner.model.edamam.Recipe
import kotlinx.android.synthetic.main.activity_recipe_detail.recipe_detail_Image
import kotlinx.android.synthetic.main.activity_recipe_detail.recipe_detail_calories
import kotlinx.android.synthetic.main.activity_recipe_detail.recipe_detail_title
import kotlinx.android.synthetic.main.activity_recipe_detail.recipee_detail_servings
import java.io.Serializable
import kotlin.math.roundToInt

class RecipeDetailActivity : AppCompatActivity(), Serializable {

  companion object {
    fun buildIntent(context: Context):Intent = Intent(context, RecipeDetailActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_recipe_detail)

    val recipe = Gson().fromJson(intent.getStringExtra("RecipeExtra"), Recipe::class.java)

    recipe_detail_title.text = recipe.label

    val servingSizeText = "${getString(string.recipe_details_serving)}${recipe.yield}"
    recipee_detail_servings.text = servingSizeText

    val calsPerServing = (recipe.calories / recipe.yield).roundToInt()
    val caloriesText = "${getString(string.recipe_detail_calories)} ${calsPerServing}"
    recipe_detail_calories.text = caloriesText

    Glide.with(this)
        .load(recipe.image)
        .into(recipe_detail_Image)
  }
}
