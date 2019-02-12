package com.zonkey.simplemealplanner.activity

import com.zonkey.simplemealplanner.model.Hit

interface MainView {

  fun setQueryTitleText(queryText: String)

  fun setEmptySearchViewVisibility(visibility: Int)

  fun setHomePageProgressVisibility(visibility: Int)

  fun setUpAdapter(recipeHits: List<Hit>)
}