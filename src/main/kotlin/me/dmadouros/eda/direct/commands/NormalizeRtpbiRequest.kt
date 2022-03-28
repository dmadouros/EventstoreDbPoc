package me.dmadouros.eda.direct.commands

import me.dmadouros.eda.direct.dtos.NormalizedPharmacyDto
import me.dmadouros.eda.direct.dtos.NormalizedPrescriptionDto
import me.dmadouros.eda.direct.dtos.NormalizedRtpbiRequestDto
import me.dmadouros.eda.direct.dtos.PharmacyDto
import me.dmadouros.eda.direct.dtos.PrescriptionDto
import me.dmadouros.eda.direct.events.RtpbiRequestNormalized
import me.dmadouros.eda.direct.events.RtpbiRequestReceived
import me.dmadouros.eda.shared.infrastructure.PharmacyRepository

class NormalizeRtpbiRequest(private val pharmacyRepository: PharmacyRepository) {
    fun call(rtpbiRequestReceived: RtpbiRequestReceived): RtpbiRequestNormalized {
        val rtpbiRequest = rtpbiRequestReceived.body

        val normalizedRtpbiRequest = NormalizedRtpbiRequestDto(
            messageId = rtpbiRequest.messageId,
            apiKey = rtpbiRequest.apiKey,
            patient = rtpbiRequest.patient,
            eligibility = rtpbiRequest.eligibility,
            prescription = toNormalizedPrescriptionDto(rtpbiRequest.prescription),
            pIdentifier = rtpbiRequest.pIdentifier
        )

        return RtpbiRequestNormalized(rtpbiRequestReceived.id, body = normalizedRtpbiRequest)
    }

    private fun toNormalizedPrescriptionDto(prescription: PrescriptionDto): NormalizedPrescriptionDto =
        NormalizedPrescriptionDto(
            ndcCode = prescription.ndcCode,
            prescriberNpi = prescription.prescriberNpi,
            daysSupply = prescription.daysSupply,
            quantity = prescription.quantity,
            pharmacy = toNormalizedPharmacy(prescription.pharmacy),
            dawCode = prescription.dawCode
        )

    private fun toNormalizedPharmacy(pharmacy: PharmacyDto): NormalizedPharmacyDto {
        val pharmacyPharmacy = pharmacyRepository.findByNpi(pharmacy.npi)
        return NormalizedPharmacyDto(
            npi = pharmacyPharmacy.npi,
            ncpdpId = pharmacyPharmacy.ncpdpId,
            name = pharmacyPharmacy.name,
            address = pharmacyPharmacy.address
        )
    }
}
