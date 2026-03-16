package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.TeamRecord
import kotlinx.serialization.Serializable

@Serializable
data class TeamRecordListDto(val data: List<TeamRecordDto>)

fun TeamRecordListDto.toDomain(): List<TeamRecord> = data.map { it.toDomain() }
