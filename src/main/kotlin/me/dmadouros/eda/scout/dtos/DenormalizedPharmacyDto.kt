package me.dmadouros.eda.scout.dtos

import me.dmadouros.eda.shared.dtos.AddressDto

data class DenormalizedPharmacyDto(
    val npi: String? = null,
    val ncpdpId: String? = null,
    val name: String? = null,
    val address: AddressDto = AddressDto(),
)
