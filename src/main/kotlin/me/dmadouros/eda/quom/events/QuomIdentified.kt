package me.dmadouros.eda.quom.events

import me.dmadouros.eda.quom.dtos.QuomDto
import me.dmadouros.eda.shared.events.Event

sealed class QuomIdentified(override val id: String) : Event {
    override val category: String = "rtpbiRequest"
}

data class QuomFound(
    override val id: String,
    val body: QuomDto
) :
    QuomIdentified(id), Event {
    override val type: String = "QuomFound"
}

// data class QuomNotFound(override val id: String) : QuomIdentified(id), Event {
//    override val type: String = "QuomNotFound"
// }
