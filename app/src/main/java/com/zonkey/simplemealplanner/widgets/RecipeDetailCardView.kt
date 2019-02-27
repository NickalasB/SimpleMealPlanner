package com.zonkey.simplemealplanner.widgets

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.core.content.ContextCompat
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.R.color
import com.zonkey.simplemealplanner.R.drawable
import com.zonkey.simplemealplanner.R.layout
import com.zonkey.simplemealplanner.R.string
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.utils.createBitmapFromDrawableRes
import kotlinx.android.synthetic.main.recipe_detail_view.view.detail_ingredients_label
import kotlinx.android.synthetic.main.recipe_detail_view.view.detail_recipe_calories
import kotlinx.android.synthetic.main.recipe_detail_view.view.detail_recipe_link
import kotlinx.android.synthetic.main.recipe_detail_view.view.detail_recipe_servings
import kotlinx.android.synthetic.main.recipe_detail_view.view.detail_recipe_source
import kotlinx.android.synthetic.main.recipe_detail_view.view.detail_recipe_title
import kotlin.math.roundToInt

class RecipeDetailCardView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  private var detailRecipeTitle: String? = null
    set(value) {
      field = value
      detail_recipe_title.text = detailRecipeTitle
    }

  private var detailRecipeServing: String? = null
    set(value) {
      field = value
      detail_recipe_servings.text = detailRecipeServing
    }

  private var detailRecipeCalories: String? = null
    set(value) {
      field = value
      detail_recipe_calories.text = detailRecipeCalories
    }

  private var detailRecipeIngredientsLabel: String? = null
    set(value) {
      field = value
      detail_ingredients_label.text = detailRecipeIngredientsLabel
    }

  private var detailRecipeSource: String? = null
    set(value) {
      field = value
      detail_recipe_source.text = detailRecipeSource
    }

  private var detailRecipeLink: String? = null
    set(value) {
      field = value
      detail_recipe_link.text = detailRecipeLink
    }

  private var detailRecipeButtonText: String? = null
    set(value) {
      field = value
      detail_recipe_link.text = detailRecipeButtonText
    }

  init {
    View.inflate(context, R.layout.recipe_detail_view, this)
    setBackgroundColor(ContextCompat.getColor(context, R.color.indexCardYellow))
    orientation = LinearLayout.VERTICAL
    setPadding(
        context.resources.getDimensionPixelSize(R.dimen.common_margin_double),
        context.resources.getDimensionPixelSize(R.dimen.common_margin_half),
        context.resources.getDimensionPixelSize(R.dimen.common_margin_double),
        0
    )

    attrs?.let {
      context.obtainStyledAttributes(it, R.styleable.RecipeDetailCardView, 0, 0).apply {
        detailRecipeTitle = getString(R.styleable.RecipeDetailCardView_title)
        detailRecipeServing = getString(R.styleable.RecipeDetailCardView_serving)
        detailRecipeCalories = getString(R.styleable.RecipeDetailCardView_calories)
        detailRecipeCalories = getString(R.styleable.RecipeDetailCardView_calories)
        detailRecipeIngredientsLabel = getString(R.styleable.RecipeDetailCardView_ingredients_label)
        detailRecipeSource = getString(R.styleable.RecipeDetailCardView_source)
        detailRecipeLink = getString(R.styleable.RecipeDetailCardView_link)
        detailRecipeButtonText = getString(R.styleable.RecipeDetailCardView_button_text)
        recycle()
      }
    }

  }

  fun setRecipeDetailCardItems(recipe: Recipe) {
    val servingSizeText = "${context.getString(R.string.recipe_details_serving)} ${recipe.yield}"
    detailRecipeServing = servingSizeText

    val calsPerServing = (recipe.calories / recipe.yield).roundToInt()
    val caloriesText = "${context.getString(R.string.recipe_detail_calories)} $calsPerServing"
    detailRecipeCalories = caloriesText

    val sourceLabel = context.getString(string.detail_recipe_source_label)
    val sourceText = "$sourceLabel ${recipe.source}"
    detailRecipeSource = sourceText

    detailRecipeLink = recipe.url

    detailRecipeButtonText = context.getString(string.detail_button_text)

    setUpDetailsButton(recipe)

    populateIngredients(recipe)
  }

  private fun setUpDetailsButton(recipe: Recipe) {
    detail_recipe_link.setOnClickListener {
      val customTabsBuilder = Builder()
      val customTabsIntent = customTabsBuilder.build()
      customTabsBuilder.setToolbarColor(ContextCompat.getColor(context, color.colorPrimary))
      val backArrowBitmap = createBitmapFromDrawableRes(context,
          drawable.ic_arrow_back_white_24dp)?.let { it }
      backArrowBitmap?.let {
        customTabsBuilder.setCloseButtonIcon(backArrowBitmap)
      }
      customTabsIntent.launchUrl(context, Uri.parse(recipe.url))
    }
  }

  private fun populateIngredients(recipe: Recipe) {
    recipe.ingredientLines.forEach {

      val ingredientLine = View.inflate(context, layout.ingredent_line, null)

      val ingredientText = ingredientLine.findViewById<TextView>(R.id.detail_ingredient_line)

      val ingredientLayout = ingredientLine.findViewById<LinearLayout>(
          R.id.detail_ingredient_linear_layout)

      val bullet = context.getString(string.bullet)
      val ingredientWithBullet = "$bullet $it"
      ingredientText.text = ingredientWithBullet

      // Ingredients are added to the 8th position in the layout
      this.addView(ingredientLayout, 8,
          android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
              android.view.ViewGroup.LayoutParams.WRAP_CONTENT))
    }
  }
}