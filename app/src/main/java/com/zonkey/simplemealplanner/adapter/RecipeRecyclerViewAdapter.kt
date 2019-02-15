package com.zonkey.simplemealplanner.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.activity.RecipeDetailActivity
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.utils.inflate
import kotlinx.android.synthetic.main.recipe_preview_view_item.view.recipe_card
import kotlinx.android.synthetic.main.recipe_preview_view.view.recipe_card_item_image


internal const val FULL_RECIPE = "full_recipe"

class RecipeRecyclerViewAdapter(
    private val clickListener: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeRecyclerViewAdapter.ViewHolder>(
    RecipeDiffCallBack()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position),
      clickListener)

  class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
      parent.inflate(R.layout.recipe_preview_view_item, false)) {

    fun bind(recipe: Recipe, listener: (Recipe) -> Unit) =
        with(itemView) {
          recipe_card.setRecipeCardItems(recipe)
          setOnClickListener {
            listener(recipe)
            val intent = RecipeDetailActivity.buildIntent(context)
            intent.putExtra(FULL_RECIPE, Gson().toJson(recipe))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity,
                recipe_card_item_image, itemView.context.getString(R.string.recipe_image_transition))
            context.startActivity(intent, options.toBundle())
          }
        }
  }

  class RecipeDiffCallBack : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem.label == newItem.label
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem == newItem
  }
}
