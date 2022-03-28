package me.dmadouros.eda.direct.dtos

import me.dmadouros.eda.shared.dtos.AddressDto

data class NormalizedPharmacyDto(
    val npi: String,
    val ncpdpId: String,
    val name: String,
    val address: AddressDto,
)
