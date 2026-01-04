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

fun PlayerDto.toDomain(): SearchItem.Player = SearchItem.Player(
    id = id,
    firstName = firstName,
    lastName = lastName
)

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String? = null
)

fun TeamDto.toDomain(): SearchItem.Team = SearchItem.Team(
    id = id,
    name = name,
    logoPath = logo
)