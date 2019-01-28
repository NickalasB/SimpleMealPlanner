package com.zonkey.simplemealplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Ingredient

class RecipeCardAdapter (private val recipes: List<Hit>) :
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
    private val recipeTitle: TextView = recipeCardView.findViewById(
        com.zonkey.simplemealplanner.R.id.recipe_card_title)
    private val recipeIngredients: TextView = recipeCardView.findViewById(
        com.zonkey.simplemealplanner.R.id.recipe_card_ingredients)
    private val recipeLink: TextView = recipeCardView.findViewById(
        com.zonkey.simplemealplanner.R.id.recipe_card_link)

    fun displayRecipe(hit: Hit) {
      val recipe = hit.recipe
      val items: List<Ingredient> = recipe.ingredients
      val ingredientsText = "Ingredients: $items"
      val recipeLinkText = "Link: ${recipe.uri}"

      recipeTitle.text = hit.recipe.label
      recipeIngredients.text = ingredientsText
      recipeLink.text = recipeLinkText
    }
  }
}