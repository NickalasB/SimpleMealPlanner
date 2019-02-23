package com.zonkey.simplemealplanner.activity

import android.app.AlertDialog.Builder
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
import com.zonkey.simplemealplanner.R.string
import com.zonkey.simplemealplanner.adapter.FROM_FAVORITE
import com.zonkey.simplemealplanner.adapter.FULL_RECIPE
import com.zonkey.simplemealplanner.firebase.FirebaseRecipeRepository
import com.zonkey.simplemealplanner.model.DayOfWeek
import com.zonkey.simplemealplanner.model.Recipe
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_collapsing_toolbar
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_favorite_button
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_image
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_recipe_parent_layout
import kotlinx.android.synthetic.main.activity_recipe_detail.detail_save_to_meal_plan_button
import kotlinx.android.synthetic.main.activity_recipe_detail.detailed_recipe_card_view
import javax.inject.Inject


class RecipeDetailActivity : AppCompatActivity(), RecipeDetailView {

  @Inject
  lateinit var firebaseRepo: FirebaseRecipeRepository

  private lateinit var presenter: RecipeDetailActivityPresenter

  override var isSavedRecipe = false

  override var addedToMealPlan = false

  private lateinit var recipe: Recipe

  companion object {
    fun buildIntent(context: Context): Intent = Intent(context, RecipeDetailActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)
    setContentView(R.layout.activity_recipe_detail)

    presenter = RecipeDetailActivityPresenter(this, firebaseRepo)

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition()
    }

    recipe = Gson().fromJson(intent.getStringExtra(FULL_RECIPE), Recipe::class.java)
    loadRecipeImage(recipe)

    detail_collapsing_toolbar.title = recipe.label

    detail_collapsing_toolbar.setCollapsedTitleTextColor(
        ContextCompat.getColor(this, R.color.textWhite))

    detailed_recipe_card_view.setRecipeDetailCardItems(recipe)

    setupFavoriteButton(recipe)

    setupMealPlanDialog(recipe)

    if (recipe.mealPlan && recipe.day.name.isNotEmpty()) {
      detail_save_to_meal_plan_button.text = recipe.day.name
    }
  }

  private fun setupFavoriteButton(recipe: Recipe) {
    isSavedRecipe = intent.getBooleanExtra(FROM_FAVORITE, false)
    presenter.setSavedRecipeIcon(isSavedRecipe)

    detail_favorite_button.setOnClickListener {
      presenter.onFavoriteButtonClicked(isSavedRecipe, recipe)
    }
  }

  private fun setupMealPlanDialog(recipe: Recipe) {
    var selectedDay: String
    detail_save_to_meal_plan_button.setOnClickListener {
      val builder = Builder(this)
      builder.setTitle(getString(R.string.detail_meal_plan_dialog_title))
      val days: Array<String> = DayOfWeek.values().map { it.name }.toTypedArray()
      selectedDay = days[0]
      builder.setSingleChoiceItems(days, 0) { _, which ->
        selectedDay = days[which]
      }
      builder.setPositiveButton(getString(string.common_ok)) { dialog, _ ->
        presenter.onMealPlanDialogPositiveButtonClicked(
            recipe = recipe,
            addedToMealPlan = addedToMealPlan,
            selectedDay = selectedDay,
            isSavedRecipe = isSavedRecipe)

        showFavoriteSnackBar(
            snackBarString = getString(string.detail_meal_plan_snackbar_text, selectedDay))
        dialog.dismiss()
      }
      builder.setNegativeButton(getString(string.common_back)) { dialog, _ ->
        dialog.dismiss()
      }
      builder.create().show()
    }
  }

  override fun setMealPlanButtonText(mealPlanButtonStringRes: Int, selectedDayString: String?) {
    val favoriteButtonText = when {
      mealPlanButtonStringRes != 0 -> getString(mealPlanButtonStringRes)
      !selectedDayString.isNullOrBlank() -> selectedDayString
      else -> ""
    }
    detail_save_to_meal_plan_button.text = favoriteButtonText
  }

  override fun showFavoriteSnackBar(snackBarStringRes: Int, snackBarString: String?) {
    val snackBarText = when {
      snackBarStringRes != 0 -> getString(snackBarStringRes)
      !snackBarString.isNullOrBlank() -> snackBarString
      else -> ""
    }
    Snackbar.make(detail_recipe_parent_layout, snackBarText,
        Snackbar.LENGTH_SHORT).show()
  }

  override fun setFavoritedButtonIcon(icon: Int) {
    detail_favorite_button.background = ContextCompat.getDrawable(this, icon)
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

  //TODO this needs some work
  override fun onDestroy() {
    super.onDestroy()
    firebaseRepo.purgeUnsavedRecipe(recipe)
  }
}
