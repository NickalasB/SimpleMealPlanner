package com.zonkey.simplemealplanner.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.model.Recipe
import kotlinx.android.synthetic.main.recipe_preview_view.view.recipe_card_item_image
import kotlinx.android.synthetic.main.recipe_preview_view.view.recipe_card_item_title

class SingleRecipeCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : LinearLayout(context, attrs, defStyleAttr) {

  private var title: String? = null
    set(value) {
      field = value
      recipe_card_item_title.text = value
      recipe_card_item_title.background = ContextCompat.getDrawable(context, R.drawable.scrim_bottom)
    }

  private var imageUrl: String? = null
    set(value) {
      field = value
      Glide.with(this)
          .load(value)
          .into(recipe_card_item_image)
    }

  init {
    View.inflate(context, R.layout.recipe_preview_view, this)
    orientation = VERTICAL
    attrs?.let {
      context.obtainStyledAttributes(it, R.styleable.SingleRecipeCard, 0, 0).apply {
        title = getString(R.styleable.SingleRecipeCard_recipe_text)
        imageUrl = getString(R.styleable.SingleRecipeCard_recipe_imageUrl)
        recycle()
      }
    }

    val params = LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.MATCH_PARENT)
    recipe_card_item_title.layoutParams = params
  }

  fun setRecipeCardItems(recipe: Recipe) {
    title = recipe.label
    imageUrl = recipe.image
  }
}