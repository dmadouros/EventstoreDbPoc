package me.dmadouros.eda.pharmacy.events

import me.dmadouros.eda.medication.dtos.MedicationDto
import me.dmadouros.eda.shared.events.Event

sealed class MedicationIdentified(override val id: String) : Event {
    override val category: String = "rtpbiRequest"
}

data class MedicationFound(
    override val id: String,
    val body: MedicationDto
) :
    MedicationIdentified(id), Event {
    override val type: String = "MedicationFound"
}

//data class MedicationNotFound(override val id: String) : MedicationIdentified(id), Event {
//    override val type: String = "MedicationNotFound"
//}
