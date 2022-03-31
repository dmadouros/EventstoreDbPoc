package me.dmadouros.eda.shared.dtos

data class AddressDto(
    val line1: String? = null,
    val line2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
)
