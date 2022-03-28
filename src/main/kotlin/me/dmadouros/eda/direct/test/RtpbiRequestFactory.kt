package me.dmadouros.eda.direct.test

import me.dmadouros.eda.direct.dtos.EligibilityDto
import me.dmadouros.eda.direct.dtos.Gender
import me.dmadouros.eda.direct.dtos.PatientDto
import me.dmadouros.eda.direct.dtos.PharmacyDto
import me.dmadouros.eda.direct.dtos.PrescriptionDto
import me.dmadouros.eda.direct.dtos.QuantityDto
import me.dmadouros.eda.direct.dtos.RtpbiRequestDto
import me.dmadouros.eda.shared.dtos.AddressDto
import java.math.BigDecimal
import java.util.UUID

object RtpbiRequestFactory {
    fun create(): RtpbiRequestDto =
        RtpbiRequestDto(
            messageId = UUID.randomUUID().toString(),
            apiKey = "9aa33d44-259a-4c90-9333-8d636c36e784",
            patient = PatientDto(
                firstName = "Bilbo",
                lastName = "Baggins",
                dateOfBirth = "1900-01-01",
                gender = Gender.MALE,
                address = AddressDto(
                    line1 = "1234 Main St.",
                    city = "Hobbiton",
                    state = "CO",
                    zipCode = "80000",
                )
            ),
            eligibility = EligibilityDto(
                pbmMemberId = "111-222-333",
                cardholderId = "1234-567-89",
            ),
            pIdentifier = "P123456789",
            prescription = PrescriptionDto(
                ndcCode = "1234567890",
                dawCode = 0,
                prescriberNpi = "1234567890",
                daysSupply = 30,
                quantity = QuantityDto(
                    value = BigDecimal.valueOf(30.0),
                    quomCode = "C12345"
                ),
                pharmacy = PharmacyDto(
                    npi = "0987654321",
                    ncpdpId = "76543231"
                )
            )
        )
}
