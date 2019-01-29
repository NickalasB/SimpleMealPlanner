package com.zonkey.simplemealplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.model.edamam.Hit
import kotlin.math.roundToInt

class RecipeCardAdapter(private val recipes: List<Hit>) :
    RecyclerView.Adapter<RecipeCardAdapter.RecipeCardViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCardViewHolder {
    val recipeCardView =
        LayoutInflater.from(parent.context).inflate(
            com.zonkey.simplemealplanner.R.layout.recipe_card, parent, false) as CardView
    return RecipeCardViewHolder(recipeCardView)
  }

  override fun getItemCount() = recipes.size

  override fun onBindViewHolder(viewHolder: RecipeCardViewHolder, position: Int) {
    viewHolder.displayRecipe(recipes[position])
  }

  class RecipeCardViewHolder(recipeCardView: CardView) : RecyclerView.ViewHolder(recipeCardView) {
    private val recipeTitle: TextView = recipeCardView.findViewById(R.id.recipe_card_title)
    private val recipeLink: TextView = recipeCardView.findViewById(R.id.recipe_card_link)
    private val recipeServing: TextView = recipeCardView.findViewById(R.id.recipe_card_servings)
    private val recipeCalories: TextView = recipeCardView.findViewById(R.id.recipe_card_calories)

    fun displayRecipe(hit: Hit) {
      val recipe = hit.recipe
      val linkText = "Link: ${recipe.url}"
      val servings = "Servings: ${recipe.yield}"
      val calsPerServing = recipe.calories.roundToInt() / recipe.yield
      val caloriesText = "Calories/serving: $calsPerServing"

      recipeTitle.text = hit.recipe.label
      recipeServing.text = servings
      recipeLink.text = linkText
      recipeCalories.text = caloriesText
    }
  }
}