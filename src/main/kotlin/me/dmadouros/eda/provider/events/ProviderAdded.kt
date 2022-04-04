package me.dmadouros.eda.provider.events

import me.dmadouros.eda.provider.dtos.ProviderDto
import me.dmadouros.eda.shared.events.Event

data class ProviderAdded(override val id: String, val body: ProviderDto) : Event {
    override val category: String = "provider"
    override val type: String = "ProviderAdded"
}
