package com.zonkey.simplemealplanner.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.zonkey.simplemealplanner.adapter.RecipeRecyclerViewAdapter
import com.zonkey.simplemealplanner.model.Recipe

private const val MILLIS_PER_INCH = 150F

class RecipeRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

  init {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    adapter = RecipeRecyclerViewAdapter {}
  }

  fun setRecipes(recipes: List<Recipe?>) {
    adapter = RecipeRecyclerViewAdapter {}
    (adapter as RecipeRecyclerViewAdapter).submitList(recipes)
  }

  fun reverseLayout() {
    (layoutManager as LinearLayoutManager).reverseLayout = true
  }

  fun smoothScrollToNewestRecipe(position: Int) {
    smoothScroller.targetPosition = position;
    layoutManager?.startSmoothScroll(smoothScroller)
  }

  private var smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
      return LinearSmoothScroller.SNAP_TO_START
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
      return MILLIS_PER_INCH / displayMetrics.densityDpi

    }
  }
}