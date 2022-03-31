package me.dmadouros.eda.direct.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class DenormalizedRtpbiRequestDto(
    val messageId: String? = null,
    val apiKey: String? = null,
    val patient: PatientDto = PatientDto(),
    val eligibility: EligibilityDto = EligibilityDto(),
    val prescription: DenormalizedPrescriptionDto = DenormalizedPrescriptionDto(),
    @get:JsonProperty("pIdentifier") val pIdentifier: String? = null,
    val location: LocationDto = LocationDto(),
)
