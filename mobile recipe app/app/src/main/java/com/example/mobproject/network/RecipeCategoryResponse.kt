package com.example.mobproject.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCategoryResponse(
    @SerialName("categories")
    val categories: List<RecipeCategory>? = null
)