package me.dmadouros.eda.provider.events

import me.dmadouros.eda.provider.dtos.ProviderDto
import me.dmadouros.eda.shared.events.Event

sealed class ProviderIdentified(override val id: String) : Event {
    override val category: String = "rtpbiRequest"
}

data class ProviderFound(
    override val id: String,
    val body: ProviderDto
) :
    ProviderIdentified(id), Event {
    override val type: String = "ProviderFound"
}

//data class ProviderNotFound(override val id: String) : ProviderIdentified(id), Event {
//    override val type: String = "ProviderNotFound"
//}
