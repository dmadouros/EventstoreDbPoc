package me.dmadouros.eda.direct.dtos

import me.dmadouros.eda.shared.dtos.AddressDto

data class PatientDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val gender: Gender? = null,
    val address: AddressDto = AddressDto(),
    val mrn: String? = null,
)
