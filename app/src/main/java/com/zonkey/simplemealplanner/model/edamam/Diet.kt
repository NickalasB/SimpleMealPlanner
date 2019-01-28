package com.zonkey.simplemealplanner.model.edamam

import com.google.gson.annotations.SerializedName

enum class Diet {
  @SerializedName("balanced")
  BALANCED,
  @SerializedName("high-fiber")
  HIGH_FIBER,
  @SerializedName("high-protein")
  HIGH_PROTEIN
}