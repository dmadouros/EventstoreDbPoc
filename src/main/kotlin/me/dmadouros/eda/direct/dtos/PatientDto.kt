package me.dmadouros.eda.direct.dtos

import me.dmadouros.eda.shared.dtos.AddressDto

data class PatientDto(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: Gender,
    val address: AddressDto,
    val mrn: String? = null,
)
