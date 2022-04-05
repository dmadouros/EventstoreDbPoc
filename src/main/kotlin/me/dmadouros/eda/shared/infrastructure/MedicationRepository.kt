package me.dmadouros.eda.shared.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.dmadouros.eda.medication.dtos.MedicationDto
import me.dmadouros.eda.medication.events.MedicationAdded

class MedicationRepository(private val objectMapper: ObjectMapper, private val messageStore: MessageStore) {
    fun findByNdc(ndc: String): MedicationDto =
        objectMapper.readValue<MedicationAdded>(
            messageStore.readEvents(
                category = "medication",
                id = ndc
            ).first().eventData
        ).body
}
