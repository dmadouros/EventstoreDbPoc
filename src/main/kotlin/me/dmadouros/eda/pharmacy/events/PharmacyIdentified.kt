package me.dmadouros.eda.pharmacy.events

import me.dmadouros.eda.pharmacy.dtos.PharmacyDto
import me.dmadouros.eda.shared.events.Event

sealed class PharmacyIdentified(override val id: String) : Event {
    override val category: String = "rtpbiRequest"
}

data class PharmacyFound(
    override val id: String,
    val body: PharmacyDto
) :
    PharmacyIdentified(id), Event {
    override val type: String = "PharmacyFound"
}

//data class PharmacyNotFound(override val id: String) : PharmacyIdentified(id), Event {
//    override val type: String = "PharmacyNotFound"
//}
