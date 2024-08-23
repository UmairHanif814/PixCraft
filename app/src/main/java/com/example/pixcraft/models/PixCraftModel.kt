package com.example.pixcraft.models

data class PixCraftModel(
    val page: Int,
    val per_page: Int,
    val photos: List<Photo>,
    val total_results: Int,
    val next_page: String
)