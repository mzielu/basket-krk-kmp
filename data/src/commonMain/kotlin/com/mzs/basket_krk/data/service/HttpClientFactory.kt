package com.mzs.basket_krk.data.service

import com.mzs.basket_krk.data.dto.ErrorDto
import com.mzs.basket_krk.domain.model.Failure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun platformEngine(): HttpClientEngine

class HttpClientFactory {
    fun create(): HttpClient = HttpClient(platformEngine()) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
            header("TRNMT", "mba")
            header("OS", "android")
            header("APP-VERSION", "100")
        }

        HttpResponseValidator {
            validateResponse { response: HttpResponse ->
                if (response.status.isSuccess()) return@validateResponse

                val error = runCatching { response.body<ErrorDto>() }.getOrNull()

                error?.let {
                    when (it.errorType) {
                        "no_data" -> throw Failure.NoDataAvailableError
                        "old_version" -> throw Failure.OldVersionError
                        else -> throw Failure.ApiError(errorType = it.errorType)
                    }
                }
                    ?: throw Failure.UnknownError(Throwable("Unknown error with code: ${response.status.value}"))
            }
        }
    }
}