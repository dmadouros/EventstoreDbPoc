package me.dmadouros.eda.medication.commands

import me.dmadouros.eda.medication.dtos.MedicationDto
import me.dmadouros.eda.medication.events.MedicationAdded
import me.dmadouros.eda.shared.infrastructure.MessageStore

class AddMedication(private val messageStore: MessageStore) {
    fun call(medication: MedicationDto) {
        val medicationAdded = MedicationAdded(id = medication.ndc, body = medication)
        messageStore.writeEvent(medicationAdded)
    }
}
