package me.dmadouros.eda.quom.events

import me.dmadouros.eda.quom.dtos.QuomDto
import me.dmadouros.eda.shared.events.Event

data class QuomAdded(override val id: String, val body: QuomDto) : Event {
    override val category: String = "quom"
    override val type: String = "QuomAdded"
}
