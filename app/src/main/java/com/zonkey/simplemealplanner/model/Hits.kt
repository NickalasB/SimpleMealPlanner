package com.zonkey.simplemealplanner.model

data class Hits(
    val q: String,
    val from: Int,
    val to: Int,
    val params: Any,
    val count: Int,
    val more: Boolean,
    val hits: List<Hit>)

