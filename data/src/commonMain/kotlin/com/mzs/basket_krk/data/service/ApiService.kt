package com.mzs.basket_krk.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiService(
    val client: HttpClient,
) {
    suspend inline fun <reified T> get(path: String): T {
        return client.get("$BASE_URL$path").body()
    }

    suspend inline fun <reified T, reified B : Any> post(path: String, body: B): T {
        return client.post("$BASE_URL$path") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    companion object {
        const val BASE_URL = "http://130.61.230.255:8000/"
    }
}
