package me.dmadouros.eda.medication.events

import me.dmadouros.eda.medication.dtos.MedicationDto
import me.dmadouros.eda.shared.events.Event

data class MedicationAdded(override val id: String, val body: MedicationDto) : Event {
    override val category: String = "medication"
    override val type: String = "MedicationAdded"
}
