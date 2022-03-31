package me.dmadouros.eda.pharmacy.commands

import me.dmadouros.eda.pharmacy.events.PharmacyFound
import me.dmadouros.eda.pharmacy.events.PharmacyIdentified
import me.dmadouros.eda.shared.infrastructure.PharmacyRepository

class IdentifyPharmacy(private val pharmacyRepository: PharmacyRepository) {
    fun call(id: String, npi: String): PharmacyIdentified {
        val pharmacy = pharmacyRepository.findByNpi(npi)

        return PharmacyFound(id, pharmacy)
    }
}
