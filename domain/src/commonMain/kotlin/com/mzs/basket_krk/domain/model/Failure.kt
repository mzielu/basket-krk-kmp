package com.mzs.basket_krk.domain.model

sealed class Failure : Throwable() {
    data object UnknownError : Failure()

    sealed class ApiError : Failure() {
        data object NetworkError : ApiError()

        data object UnknownApiError : ApiError()

        data class HttpError(val code: Int, val errorBody: String) : ApiError()
    }
}
