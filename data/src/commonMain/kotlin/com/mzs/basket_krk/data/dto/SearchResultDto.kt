package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.data.serializer.SearchResultDtoSerializer
import com.mzs.basket_krk.domain.model.SearchItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = SearchResultDtoSerializer::class)
sealed class SearchResultDto {
    abstract val type: String

    @Serializable
    data class Player(
        override val type: String = "player",
        val data: PlayerDto
    ) : SearchResultDto()

    @Serializable
    data class Team(
        override val type: String = "team",
        val data: TeamDto
    ) : SearchResultDto()
}

@Serializable
data class PlayerDto(
    val id: Int,
    @SerialName("fn") val firstName: String,
    @SerialName("ln") val lastName: String,
)

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String? = null
)

fun SearchResultDto.toDomain(): SearchItem =
    when (this) {
        is SearchResultDto.Player -> SearchItem.Player(
            id = data.id,
            firstName = data.firstName,
            lastName = data.lastName
        )

        is SearchResultDto.Team -> SearchItem.Team(
            id = data.id,
            name = data.name,
            logoPath = data.logo
        )
    }