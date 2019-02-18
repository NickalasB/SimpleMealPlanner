package com.zonkey.simplemealplanner.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zonkey.simplemealplanner.adapter.RecipeRecyclerViewAdapter
import com.zonkey.simplemealplanner.model.Recipe

private const val COLUMN_COUNT = 3

class RecipeRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

  var isFavorite: Boolean? = null

  init {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    adapter = RecipeRecyclerViewAdapter {}
  }

  fun setRecipes(recipes: List<Recipe?>) {
    adapter = RecipeRecyclerViewAdapter(isFavorite) {}
    (adapter as RecipeRecyclerViewAdapter).submitList(recipes)
  }
}