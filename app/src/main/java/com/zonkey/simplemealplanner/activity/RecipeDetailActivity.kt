package com.zonkey.simplemealplanner.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.R.drawable
import com.zonkey.simplemealplanner.adapter.FROM_FAVORITE
import com.zonkey.simplemealplanner.adapter.FULL_RECIPE
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.Recipe
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_collapsing_toolbar
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_favorite_button
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_image
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_parent_layout
import kotlinx.android.synthetic.main.activity_recipe_detail.detailed_recipe_card_view
import java.io.Serializable
import javax.inject.Inject


class RecipeDetailActivity : AppCompatActivity(), Serializable {

  @Inject
  lateinit var firebaseRepo: FirebaseRecipeRepository

  companion object {
    fun buildIntent(context: Context): Intent = Intent(context, RecipeDetailActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)
    setContentView(R.layout.activity_recipe_detail)

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition()
    }

    val recipe = Gson().fromJson(intent.getStringExtra(FULL_RECIPE), Recipe::class.java)
    loadRecipeImage(recipe)

    detail_collapsing_toolbar.title = recipe.label

    detail_collapsing_toolbar.setCollapsedTitleTextColor(
        ContextCompat.getColor(this, R.color.whiteText))

    detailed_recipe_card_view.setRecipeDetailCardItems(recipe)

    setupFavoriteButton(recipe)
  }

  private fun setupFavoriteButton(recipe: Recipe) {
    val savedRecipe = intent.getBooleanExtra(FROM_FAVORITE, false)

    setSavedRecipeIcon(savedRecipe)

    detail_favorite_button.setOnClickListener {
      if (savedRecipe) {
        firebaseRepo.deleteRecipeFromFirebase(recipe)
        setSavedRecipeIcon(savedRecipe = false)
        Snackbar.make(detail_recipe_parent_layout, getString(R.string.snackbar_recipe_deleted),
            Snackbar.LENGTH_SHORT).show()
      } else {
        firebaseRepo.saveRecipeToFirebase(recipe)
        setSavedRecipeIcon(savedRecipe = true)
        Snackbar.make(detail_recipe_parent_layout, getString(R.string.snackbar_recipe_saved),
            Snackbar.LENGTH_SHORT).show()
      }
    }
  }

  private fun setSavedRecipeIcon(savedRecipe: Boolean) {
    if (savedRecipe) {
      detail_favorite_button.background = ContextCompat.getDrawable(this,
          drawable.ic_favorite_red_24dp)
    } else {
      detail_favorite_button.background = ContextCompat.getDrawable(this,
          drawable.ic_favorite_border_red_24dp)
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
}
