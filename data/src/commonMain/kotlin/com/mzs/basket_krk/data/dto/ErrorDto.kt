package com.mzs.basket_krk.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    @SerialName("error_type") val errorType: String,
    val message: String? = null,
)