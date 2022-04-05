package me.dmadouros.eda.scout.dtos

data class DenormalizedPrescriptionDto(
    val ndc: DenormalizedMedicationDto = DenormalizedMedicationDto(),
    val prescriber: DenormalizedProviderDto = DenormalizedProviderDto(),
    val daysSupply: Int? = null,
    val quantity: DenormalizeQuantityDto = DenormalizeQuantityDto(),
    val pharmacy: DenormalizedPharmacyDto = DenormalizedPharmacyDto(),
    val dawCode: Int? = null,
)
