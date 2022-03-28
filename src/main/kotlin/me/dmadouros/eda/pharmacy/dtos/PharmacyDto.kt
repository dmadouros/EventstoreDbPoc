package me.dmadouros.eda.pharmacy.dtos

import me.dmadouros.eda.shared.dtos.AddressDto

data class PharmacyDto(
    val npi: String,
    val ncpdpId: String,
    val name: String,
    val address: AddressDto,
)
