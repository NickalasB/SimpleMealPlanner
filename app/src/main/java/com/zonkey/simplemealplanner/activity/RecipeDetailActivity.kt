package com.zonkey.simplemealplanner.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.adapter.FULL_RECIPE
import com.zonkey.simplemealplanner.model.Recipe
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_image
import kotlinx.android.synthetic.main.activity_recipe_detail.detailed_recipe_card_view
import java.io.Serializable


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

    detailed_recipe_card_view.setRecipeDetailCardItems(recipe)

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
}
