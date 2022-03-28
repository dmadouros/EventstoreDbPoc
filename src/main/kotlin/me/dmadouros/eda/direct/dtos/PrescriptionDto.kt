package me.dmadouros.eda.direct.dtos

data class PrescriptionDto(
    val ndcCode: String,
    val prescriberNpi: String,
    val daysSupply: Int,
    val quantity: QuantityDto,
    val pharmacy: PharmacyDto,
    val dawCode: Int,
)
