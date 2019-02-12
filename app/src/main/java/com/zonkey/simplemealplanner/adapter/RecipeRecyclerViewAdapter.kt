package com.zonkey.simplemealplanner.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.activity.RecipeDetailActivity
import com.zonkey.simplemealplanner.model.Recipe
import com.zonkey.simplemealplanner.utils.inflate
import kotlinx.android.synthetic.main.recipe_card.view.recipe_card

internal const val FULL_RECIPE = "full_recipe"

class RecipeRecyclerViewAdapter(
    private val clickListener: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeRecyclerViewAdapter.ViewHolder>(
    RecipeDiffCallBack()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position),
      clickListener)

  class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
      parent.inflate(R.layout.recipe_card, false)) {

    fun bind(recipe: Recipe, listener: (Recipe) -> Unit) =
        with(itemView) {
          recipe_card.setRecipeCardItems(recipe)
          setOnClickListener {
            listener(recipe)
            val intent = RecipeDetailActivity.buildIntent(context)
            intent.putExtra(FULL_RECIPE, Gson().toJson(recipe))
            context.startActivity(intent)
          }
        }
  }

  class RecipeDiffCallBack : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem.label == newItem.label
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem == newItem
  }
}
