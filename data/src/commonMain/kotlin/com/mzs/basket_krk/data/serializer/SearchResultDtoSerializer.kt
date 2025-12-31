package com.mzs.basket_krk.data.serializer

import com.mzs.basket_krk.data.dto.SearchResultDto
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object SearchResultDtoSerializer : KSerializer<SearchResultDto> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): SearchResultDto {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw (RuntimeException("This serializer can be used only with Json format"))

        val element = jsonDecoder.decodeJsonElement()
        val obj = element.jsonObject
        val type = obj["type"]?.jsonPrimitive?.content
            ?: throw (RuntimeException("Missing type field"))

        return when (type) {
            "player" -> jsonDecoder.json.decodeFromJsonElement(
                SearchResultDto.Player.serializer(),
                element
            )

            "team" -> jsonDecoder.json.decodeFromJsonElement(
                SearchResultDto.Team.serializer(),
                element
            )

            else -> {
                throw (IllegalArgumentException("Unknown type: $type"))
            }
        }
    }

    override fun serialize(encoder: Encoder, value: SearchResultDto) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw RuntimeException("This serializer can be used only with Json format")

        val element = when (value) {
            is SearchResultDto.Player -> jsonEncoder.json.encodeToJsonElement(
                SearchResultDto.Player.serializer(),
                value
            )

            is SearchResultDto.Team -> jsonEncoder.json.encodeToJsonElement(
                SearchResultDto.Team.serializer(),
                value
            )
        }
        jsonEncoder.encodeJsonElement(element)
    }
}
