package me.dmadouros.eda.shared.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.dmadouros.eda.medication.events.MedicationAdded
import me.dmadouros.eda.medication.dtos.MedicationDto
import me.dmadouros.eda.provider.dtos.ProviderDto
import me.dmadouros.eda.provider.events.ProviderAdded

class ProviderRepository(private val objectMapper: ObjectMapper, private val messageStore: MessageStore) {
    fun findByNpi(npi: String): ProviderDto =
        objectMapper.readValue<ProviderAdded>(
            messageStore.readEvents(
                category = "provider",
                id = npi
            ).first().eventData
        ).body
}
