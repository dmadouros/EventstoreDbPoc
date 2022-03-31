package me.dmadouros.eda.pharmacy.commands

import me.dmadouros.eda.pharmacy.dtos.PharmacyDto
import me.dmadouros.eda.pharmacy.events.PharmacyAdded
import me.dmadouros.eda.shared.infrastructure.MessageStore

class AddPharmacy(private val messageStore: MessageStore) {
    fun call(pharmacy: PharmacyDto) {
        val pharmacyAdded = PharmacyAdded(id = pharmacy.npi, body = pharmacy)
        messageStore.writeEvent(pharmacyAdded)
    }
}
