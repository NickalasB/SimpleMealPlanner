package com.zonkey.simplemealplanner.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zonkey.simplemealplanner.adapter.RecipeRecyclerViewAdapter
import com.zonkey.simplemealplanner.model.edamam.Recipe

class RecipeRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

  init {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    adapter = RecipeRecyclerViewAdapter {}
  }

  fun setRecipes(recipes: List<Recipe>) = (adapter as RecipeRecyclerViewAdapter).submitList(recipes)
}