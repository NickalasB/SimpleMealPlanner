package com.zonkey.simplemealplanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zonkey.simplemealplanner.R
import com.zonkey.simplemealplanner.model.edamam.Hit
import com.zonkey.simplemealplanner.model.edamam.Recipe

class RecipeCardAdapter(
    private val recipeHits: List<Hit>,
    private val clickListener: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeCardAdapter.RecipeCardViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCardViewHolder {
    val recipeCardView =
        LayoutInflater.from(parent.context).inflate(
            com.zonkey.simplemealplanner.R.layout.recipe_card, parent, false) as CardView
    return RecipeCardViewHolder(recipeCardView)
  }

  override fun getItemCount() = recipeHits.size

  override fun onBindViewHolder(viewHolder: RecipeCardViewHolder, position: Int) {
    viewHolder.bind(recipeHits[position], viewHolder.itemView, clickListener)
  }

  class RecipeCardViewHolder(recipeCardView: CardView) : RecyclerView.ViewHolder(recipeCardView) {
    private val recipeTitle: TextView = recipeCardView.findViewById(R.id.recipe_card_title)
    private val recipeImageView: ImageView = recipeCardView.findViewById(R.id.recipe_card_image)

    fun bind(hit: Hit, itemView: View, listener: (Recipe) -> Unit) = with(itemView) {

      setOnClickListener {
        Toast.makeText(itemView.context, "Clicked", Toast.LENGTH_LONG).show()
        listener(hit.recipe)
      }

      recipeTitle.text = hit.recipe.label
      Glide.with(itemView)
          .load(hit.recipe.image)
          .into(recipeImageView)
    }

  }
}