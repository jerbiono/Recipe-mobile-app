package com.example.mobproject.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCategory(
    @SerialName("idCategory")
    val idCategory: String? = null,

    @SerialName("strCategory")
    val strCategory: String? = null,

    @SerialName("strCategoryThumb")
    val strCategoryThumb: String? = null,

    @SerialName("strCategoryDescription")
    val strCategoryDescription: String? = null
)