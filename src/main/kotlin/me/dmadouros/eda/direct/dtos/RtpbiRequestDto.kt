package me.dmadouros.eda.direct.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class RtpbiRequestDto(
    val messageId: String,
    val apiKey: String,
    val patient: PatientDto,
    val eligibility: EligibilityDto,
    val prescription: PrescriptionDto,
    @get:JsonProperty("pIdentifier") val pIdentifier: String,
    val location: LocationDto = LocationDto(),
)
