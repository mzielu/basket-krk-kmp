package com.mzs.basket_krk.domain.model

sealed class Failure : Throwable() {
    data class UnknownError(val throwable: Throwable) : Failure()

    data object OldVersionError : Failure()

    data object NoDataAvailableError : Failure()

    data class ApiError(val errorType: String) : Failure()

    data object NetworkConnectionError : Failure()

}
