package me.dmadouros.eda.shared.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.dmadouros.eda.pharmacy.dtos.PharmacyDto
import me.dmadouros.eda.pharmacy.events.PharmacyAdded

class PharmacyRepository(private val objectMapper: ObjectMapper, private val messageStore: MessageStore) {
    fun findByNpi(npi: String): PharmacyDto =
        objectMapper.readValue<PharmacyAdded>(
            messageStore.readEvents(
                category = "pharmacy",
                id = npi
            ).first().eventData
        ).body
}
