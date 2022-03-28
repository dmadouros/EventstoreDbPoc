package me.dmadouros.eda.pharmacy.events

import me.dmadouros.eda.shared.events.Event
import me.dmadouros.eda.pharmacy.dtos.PharmacyDto

data class PharmacyAdded(override val id: String, val body: PharmacyDto) : Event {
    override val category: String = "pharmacy"
    override val type: String = "PharmacyAdded"
}
