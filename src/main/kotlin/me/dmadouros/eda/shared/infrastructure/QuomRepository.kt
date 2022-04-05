package me.dmadouros.eda.shared.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.dmadouros.eda.quom.dtos.QuomDto
import me.dmadouros.eda.quom.events.QuomAdded

class QuomRepository(private val objectMapper: ObjectMapper, private val messageStore: MessageStore) {
    fun findByNcitCode(ncitCode: String): QuomDto =
        objectMapper.readValue<QuomAdded>(
            messageStore.readEvents(
                category = "quom",
                id = ncitCode
            ).first().eventData
        ).body
}
