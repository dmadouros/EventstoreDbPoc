package me.dmadouros.eda.direct.dtos

data class DenormalizedPrescriptionDto(
    val ndc: DenormalizedMedicationDto = DenormalizedMedicationDto(),
    val prescriber: DenormalizedProviderDto = DenormalizedProviderDto(),
    val daysSupply: Int? = null,
    val quantity: QuantityDto = QuantityDto(),
    val pharmacy: DenormalizedPharmacyDto = DenormalizedPharmacyDto(),
    val dawCode: Int? = null,
)
