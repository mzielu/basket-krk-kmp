package com.mzs.basket_krk.presentation.base

import com.mzs.basket_krk.domain.model.Failure

data class ViewStateData<T>(
    val data: T,
    val isLoading: Boolean = false,
    val error: Failure? = null
) {
    val isError = error != null

    fun loading() = copy(isLoading = true, error = null)

    fun error(error: Failure) = copy(isLoading = false, error = error)

    fun data(data: T) = copy(isLoading = false, data = data)

    fun noNewData() = copy(isLoading = false)

    fun noError() = copy(error = null)
}
