package me.dmadouros.eda.provider.commands

import me.dmadouros.eda.provider.dtos.ProviderDto
import me.dmadouros.eda.provider.events.ProviderAdded
import me.dmadouros.eda.shared.infrastructure.MessageStore

class AddProvider(private val messageStore: MessageStore) {
    fun call(provider: ProviderDto) {
        val providerAdded = ProviderAdded(id = provider.npi, body = provider)
        messageStore.writeEvent(providerAdded)
    }
}
