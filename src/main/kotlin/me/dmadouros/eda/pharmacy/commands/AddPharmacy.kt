package me.dmadouros.eda.pharmacy.commands

import me.dmadouros.eda.pharmacy.dtos.PharmacyDto
import me.dmadouros.eda.pharmacy.events.PharmacyAdded

object AddPharmacy {
    fun call(pharmacy: PharmacyDto): PharmacyAdded {
        return PharmacyAdded(id = pharmacy.npi, body = pharmacy)
    }
}
