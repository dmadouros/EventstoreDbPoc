package me.dmadouros.eda.direct.dtos

data class DenormalizedPrescriptionDto(
    val ndcCode: String? = null,
    val prescriberNpi: String? = null,
    val daysSupply: Int? = null,
    val quantity: QuantityDto = QuantityDto(),
    val pharmacy: DenormalizedPharmacyDto = DenormalizedPharmacyDto(),
    val dawCode: Int? = null,
)
