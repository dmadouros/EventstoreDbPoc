package me.dmadouros.eda.medication.commands

import me.dmadouros.eda.medication.events.MedicationFound
import me.dmadouros.eda.medication.events.MedicationIdentified
import me.dmadouros.eda.shared.infrastructure.MedicationRepository

class IdentifyMedication(private val medicationRepository: MedicationRepository) {
    fun call(id: String, npi: String): MedicationIdentified {
        val medication = medicationRepository.findByNdc(npi)

        return MedicationFound(id, medication)
    }
}
