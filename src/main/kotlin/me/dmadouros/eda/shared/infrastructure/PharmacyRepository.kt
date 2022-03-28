package me.dmadouros.eda.shared.infrastructure

import me.dmadouros.eda.pharmacy.dtos.PharmacyDto
import me.dmadouros.eda.pharmacy.events.PharmacyAdded

class PharmacyRepository(private val messageStore: MessageStore) {
    fun findByNpi(npi: String): PharmacyDto =
        messageStore.readEvents<PharmacyAdded>(
            category = "pharmacy",
            id = npi
        ).first().body
}
