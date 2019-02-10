package com.zonkey.simplemealplanner.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.adapter.FULL_RECIPE
import com.zonkey.simplemealplanner.model.edamam.Recipe
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_collapsing_toolbar
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_ingredients_label
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_calories
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_image
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_servings
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_title
import java.io.Serializable
import kotlin.math.roundToInt


class RecipeDetailActivity : AppCompatActivity(), Serializable {

  companion object {
    fun buildIntent(context: Context): Intent = Intent(context, RecipeDetailActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_recipe_detail)

    val recipe = Gson().fromJson(intent.getStringExtra(FULL_RECIPE), Recipe::class.java)
    loadRecipeImage(recipe)

    detail_recipe_title.text = getString(R.string.recipe_detail_card_title)
    detail_collapsing_toolbar.title = recipe.label

    val servingSizeText = "${getString(R.string.recipe_details_serving)} ${recipe.yield}"
    detail_recipe_servings.text = servingSizeText

    val calsPerServing = (recipe.calories / recipe.yield).roundToInt()
    val caloriesText = "${getString(R.string.recipe_detail_calories)} $calsPerServing"
    detail_recipe_calories.text = caloriesText

    detail_ingredients_label.text = getString(R.string.detail_ingredients_label)

    setUpIngredients(recipe)
  }

  private fun loadRecipeImage(recipe: Recipe) {
    Glide.with(this)
        .load(recipe.image)
        .into(detail_recipe_image)
  }

  private fun setUpIngredients(recipe: Recipe) {
    recipe.ingredientLines.forEach {

      val ingredientLine = View.inflate(this, R.layout.ingredent_line, null)

      val ingredientText = ingredientLine.findViewById<TextView>(R.id.detail_ingredient_line)

      val bullet = getString(R.string.bullet)
      val ingredientWithBullet = "$bullet $it"
      ingredientText.text = ingredientWithBullet

      val detailLayout: ViewGroup = findViewById(R.id.details_linear_layout)
      detailLayout.addView(ingredientText, detailLayout.childCount,
          LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }
  }
}
