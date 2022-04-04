package me.dmadouros.eda.direct

import com.eventstore.dbclient.RecordedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.UUID
import me.dmadouros.eda.direct.commands.UpdateRtpbiRequestCount
import me.dmadouros.eda.direct.dtos.DenormalizedMedicationDto
import me.dmadouros.eda.direct.dtos.DenormalizedPharmacyDto
import me.dmadouros.eda.direct.dtos.DenormalizedPrescriptionDto
import me.dmadouros.eda.direct.dtos.DenormalizedProviderDto
import me.dmadouros.eda.direct.dtos.DenormalizedRtpbiRequestDto
import me.dmadouros.eda.direct.events.RtpbiRequestReceived
import me.dmadouros.eda.direct.test.RtpbiRequestFactory
import me.dmadouros.eda.medication.commands.IdentifyMedication
import me.dmadouros.eda.pharmacy.commands.IdentifyPharmacy
import me.dmadouros.eda.pharmacy.events.MedicationFound
import me.dmadouros.eda.pharmacy.events.PharmacyFound
import me.dmadouros.eda.provider.commands.IdentifyProvider
import me.dmadouros.eda.provider.events.ProviderFound
import me.dmadouros.eda.shared.infrastructure.MedicationRepository
import me.dmadouros.eda.shared.infrastructure.MessageStore
import me.dmadouros.eda.shared.infrastructure.PharmacyRepository
import me.dmadouros.eda.shared.infrastructure.ProviderRepository

object Projection {
    var count: Int = 0

    fun updateCount() = count++
}

fun Application.configureDirect(
    messageStore: MessageStore,
    objectMapper: ObjectMapper,
    pharmacyRepository: PharmacyRepository,
    medicationRepository: MedicationRepository,
    providerRepository: ProviderRepository,
) {
    messageStore.subscribe(
        category = "rtpbiRequest",
        subscriberId = "components:updateRtpbiRequestCount",
        fromStart = true
    ) {
        if (it.originalEvent.eventType == "RtpbiRequestReceived") {
            UpdateRtpbiRequestCount.call()
        }
    }
    messageStore.subscribe(
        category = "rtpbiRequest",
        subscriberId = "components:identifyPharmacy",
    ) {
        println("subscribing to RtpbiRequest in identifyPharmacy")
        if (it.originalEvent.eventType == "RtpbiRequestReceived") {
            try {
                println("identifyPharmacy received RtpbiRequest")
                val rtpbiRequestReceived: RtpbiRequestReceived = objectMapper.readValue(it.originalEvent.eventData)
                val pharmacyIdentified = IdentifyPharmacy(pharmacyRepository).call(
                    rtpbiRequestReceived.id,
                    rtpbiRequestReceived.body.prescription.pharmacy.npi
                )
                messageStore.writeEvent(pharmacyIdentified)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
    messageStore.subscribe(
        category = "rtpbiRequest",
        subscriberId = "components:identifyMedication",
    ) {
        println("subscribing to RtpbiRequest in identifyMedication")
        if (it.originalEvent.eventType == "RtpbiRequestReceived") {
            try {
                println("identifyMedication received RtpbiRequest")
                val rtpbiRequestReceived: RtpbiRequestReceived = objectMapper.readValue(it.originalEvent.eventData)
                val medicationIdentified = IdentifyMedication(medicationRepository).call(
                    rtpbiRequestReceived.id,
                    rtpbiRequestReceived.body.prescription.ndcCode
                )
                messageStore.writeEvent(medicationIdentified)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
    messageStore.subscribe(
        category = "rtpbiRequest",
        subscriberId = "components:identifyProvider",
    ) {
        if (it.originalEvent.eventType == "RtpbiRequestReceived") {
            try {
                val rtpbiRequestReceived: RtpbiRequestReceived = objectMapper.readValue(it.originalEvent.eventData)
                val providerIdentified = IdentifyProvider(providerRepository).call(
                    rtpbiRequestReceived.id,
                    rtpbiRequestReceived.body.prescription.prescriberNpi
                )
                messageStore.writeEvent(providerIdentified)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }


    routing {
        get("/rtpbiRequestCount") {
            call.respond(mapOf("count" to Projection.count))
        }

        get("/receiveRtpbiRequest") {
            RtpbiRequestReceived(UUID.randomUUID().toString(), RtpbiRequestFactory.create())
                .let { event -> messageStore.writeEvent(event) }
                .also { call.respond(HttpStatusCode.OK) }
        }

        get("/denormalizedRequests/{id}") {
            call.parameters["id"]
                ?.let { id -> messageStore.readEvents(category = "rtpbiRequest", id = id) }
                ?.let { events -> denormalizeRtpbiRequest(objectMapper, events) }
                ?.let { denormalizedRtpbiRequest -> call.respond(denormalizedRtpbiRequest) }
                ?: call.respond(HttpStatusCode.BadRequest)
        }
    }
}

private fun denormalizeRtpbiRequest(
    objectMapper: ObjectMapper,
    events: List<RecordedEvent>
): DenormalizedRtpbiRequestDto {
    val rtpbiRequestReceived =
        { denormalizedRtpbiRequestDto: DenormalizedRtpbiRequestDto, recordedEvent: RecordedEvent ->
            val rtpbiRequest = objectMapper.readValue<RtpbiRequestReceived>(recordedEvent.eventData).body
            denormalizedRtpbiRequestDto.copy(
                messageId = rtpbiRequest.messageId,
                apiKey = rtpbiRequest.apiKey,
                patient = rtpbiRequest.patient,
                eligibility = rtpbiRequest.eligibility,
                prescription = DenormalizedPrescriptionDto(
                    daysSupply = rtpbiRequest.prescription.daysSupply,
                    quantity = rtpbiRequest.prescription.quantity,
                    dawCode = rtpbiRequest.prescription.dawCode,
                ),
                pIdentifier = rtpbiRequest.pIdentifier,
                location = rtpbiRequest.location,
            )
        }
    val pharmacyFound =
        { denormalizedRtpbiRequestDto: DenormalizedRtpbiRequestDto, recordedEvent: RecordedEvent ->
            val pharmacy = objectMapper.readValue<PharmacyFound>(recordedEvent.eventData).body
            denormalizedRtpbiRequestDto.copy(
                prescription = denormalizedRtpbiRequestDto.prescription.copy(
                    pharmacy = DenormalizedPharmacyDto(
                        npi = pharmacy.npi,
                        ncpdpId = pharmacy.ncpdpId,
                        name = pharmacy.name,
                        address = pharmacy.address,
                    )
                )
            )
        }

    val medicationFound =
        { denormalizeRtpbiRequestDto: DenormalizedRtpbiRequestDto, recordedEvent: RecordedEvent ->
            val medication = objectMapper.readValue<MedicationFound>(recordedEvent.eventData).body
            denormalizeRtpbiRequestDto.copy(
                prescription = denormalizeRtpbiRequestDto.prescription.copy(
                    ndc = DenormalizedMedicationDto(
                        ndcCode = medication.ndc,
                        name = medication.name,
                    )
                )
            )
        }
    val providerFound =
        { denormalizeRtpbiRequestDto: DenormalizedRtpbiRequestDto, recordedEvent: RecordedEvent ->
            val provider = objectMapper.readValue<ProviderFound>(recordedEvent.eventData).body
            denormalizeRtpbiRequestDto.copy(
                prescription = denormalizeRtpbiRequestDto.prescription.copy(
                    prescriber = DenormalizedProviderDto(
                        npi = provider.npi,
                        name = "${provider.firstName} ${provider.lastName}, ${provider.credentials}"
                    )
                )
            )
        }

    val projection = mapOf(
        "ProviderFound" to providerFound,
        "MedicationFound" to medicationFound,
        "PharmacyFound" to pharmacyFound,
        "RtpbiRequestReceived" to rtpbiRequestReceived,
    )

    return project(DenormalizedRtpbiRequestDto(), events, projection)
}

private fun <T> project(
    init: T,
    events: List<RecordedEvent>,
    projection: Map<String, (T, RecordedEvent) -> T>
) = events.fold(init) { memo, event ->
    projection[event.eventType]?.let { handler -> handler(memo, event) } ?: memo
}
