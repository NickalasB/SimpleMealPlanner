package com.zonkey.simplemealplanner.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.zonkey.simplemealplanner.R
import kotlinx.android.synthetic.main.create_recipe_detail_view.view.create_detail_ingredient_line
import kotlinx.android.synthetic.main.create_recipe_detail_view.view.create_ingredients_label
import kotlinx.android.synthetic.main.create_recipe_detail_view.view.create_recipe_save_button
import kotlinx.android.synthetic.main.create_recipe_detail_view.view.create_recipe_title

class CreateRecipeDetailCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  private var detailRecipeTitle: String? = null
    set(value) {
      field = value
      create_recipe_title.text = detailRecipeTitle
    }

  private var detailRecipeIngredientsLabel: String? = null
    set(value) {
      field = value
      create_ingredients_label.text = detailRecipeIngredientsLabel
    }

  private var detailRecipeButtonText: String? = null
    set(value) {
      field = value
      create_recipe_save_button.text = detailRecipeButtonText
    }

  init {
    View.inflate(context, R.layout.create_recipe_detail_view, this)
    setBackgroundColor(
        ContextCompat.getColor(context, R.color.indexCardYellow))
    orientation = VERTICAL
    setPadding(
        context.resources.getDimensionPixelSize(
            R.dimen.common_margin_double),
        context.resources.getDimensionPixelSize(
            R.dimen.common_margin_half),
        context.resources.getDimensionPixelSize(
            R.dimen.common_margin_double),
        0
    )

    attrs?.let {
      context.obtainStyledAttributes(it,
          R.styleable.CreateRecipeDetailCardView, 0, 0).apply {
        detailRecipeTitle = getString(
            R.styleable.CreateRecipeDetailCardView_title)
        detailRecipeIngredientsLabel = getString(
            R.styleable.CreateRecipeDetailCardView_ingredients_label)
        detailRecipeButtonText = getString(
            R.styleable.CreateRecipeDetailCardView_button_text)
        recycle()
      }
    }

    create_detail_ingredient_line.hint = context.getString(R.string.add_recipe_ingredient_line_hint,
        "1")
  }

  internal fun addNewRecipeLine() {

    val ingredientLine = View.inflate(context, R.layout.create_ingredient_line, null)
    val ingredientEditText = ingredientLine.findViewById<IndexCardEditText>(
        R.id.create_detail_ingredient_line)

    addView(ingredientEditText, (childCount - 2),
        android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT))

    val ingredientNumber = (childCount - 4).toString()

    ingredientEditText.hint = context.getString(R.string.add_recipe_ingredient_line_hint,
        ingredientNumber)
  }
}