package me.dmadouros.eda.provider.commands

import me.dmadouros.eda.provider.events.ProviderFound
import me.dmadouros.eda.provider.events.ProviderIdentified
import me.dmadouros.eda.shared.infrastructure.ProviderRepository

class IdentifyProvider(private val providerRepository: ProviderRepository) {
    fun call(id: String, npi: String): ProviderIdentified {
        val provider = providerRepository.findByNpi(npi)

        return ProviderFound(id, provider)
    }
}
