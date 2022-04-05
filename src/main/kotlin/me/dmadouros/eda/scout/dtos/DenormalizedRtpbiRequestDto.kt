package me.dmadouros.eda.scout.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import me.dmadouros.eda.direct.dtos.EligibilityDto
import me.dmadouros.eda.direct.dtos.LocationDto
import me.dmadouros.eda.direct.dtos.PatientDto

data class DenormalizedRtpbiRequestDto(
    val messageId: String? = null,
    val apiKey: String? = null,
    val patient: PatientDto = PatientDto(),
    val eligibility: EligibilityDto = EligibilityDto(),
    val prescription: DenormalizedPrescriptionDto = DenormalizedPrescriptionDto(),
    @get:JsonProperty("pIdentifier") val pIdentifier: String? = null,
    val location: LocationDto = LocationDto(),
)
