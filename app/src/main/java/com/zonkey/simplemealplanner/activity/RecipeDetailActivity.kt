package com.zonkey.simplemealplanner.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.adapter.FULL_RECIPE
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.utils.createBitmapFromDrawableRes
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_collapsing_toolbar
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_ingredients_label
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_calories
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_image
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_link
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_servings
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_source
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

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition()
    }

    val recipe = Gson().fromJson(intent.getStringExtra(FULL_RECIPE), Recipe::class.java)
    loadRecipeImage(recipe)

    detail_recipe_title.text = getString(R.string.recipe_detail_card_title)
    detail_collapsing_toolbar.title = recipe.label
    detail_collapsing_toolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.whiteText))

    val servingSizeText = "${getString(R.string.recipe_details_serving)} ${recipe.yield}"
    detail_recipe_servings.text = servingSizeText

    val calsPerServing = (recipe.calories / recipe.yield).roundToInt()
    val caloriesText = "${getString(R.string.recipe_detail_calories)} $calsPerServing"
    detail_recipe_calories.text = caloriesText

    detail_ingredients_label.text = getString(R.string.detail_ingredients_label)

    setUpIngredients(recipe)

    val sourceText = "Source: ${recipe.source}"
    detail_recipe_source.text = sourceText

    setUpRecipeLinkButton(recipe)
  }

  private fun setUpRecipeLinkButton(recipe: Recipe) {
    detail_recipe_link.setOnClickListener {
      val customTabsBuilder = CustomTabsIntent.Builder()
      val customTabsIntent = customTabsBuilder.build()
      customTabsBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
      val backArrowBitmap = createBitmapFromDrawableRes(this, R.drawable.ic_arrow_back_white_24dp)?.let { it }
      backArrowBitmap?.let {
        customTabsBuilder.setCloseButtonIcon(backArrowBitmap)
      }
      customTabsIntent.launchUrl(this, Uri.parse(recipe.url))
    }
  }

  private fun loadRecipeImage(recipe: Recipe) {
    Glide.with(this)
        .load(recipe.image)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
              isFirstResource: Boolean): Boolean {
            return false
          }

          override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
              dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
              startPostponedEnterTransition()
            }
            return false
          }

        })
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
      // Ingredients are added at 2nd to last position in the layout
      detailLayout.addView(ingredientText, 4,
          LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }
  }
}
