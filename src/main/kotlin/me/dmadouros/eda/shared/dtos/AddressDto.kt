package me.dmadouros.eda.shared.dtos

data class AddressDto(
    val line1: String,
    val line2: String? = null,
    val city: String,
    val state: String,
    val zipCode: String,
)
