package me.dmadouros.eda.direct.dtos

data class NormalizedPrescriptionDto(
    val ndcCode: String,
    val prescriberNpi: String,
    val daysSupply: Int,
    val quantity: QuantityDto,
    val pharmacy: NormalizedPharmacyDto,
    val dawCode: Int,
)
