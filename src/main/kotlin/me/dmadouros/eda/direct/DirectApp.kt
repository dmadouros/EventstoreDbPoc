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
import me.dmadouros.eda.direct.dtos.DenormalizedPharmacyDto
import me.dmadouros.eda.direct.dtos.DenormalizedPrescriptionDto
import me.dmadouros.eda.direct.dtos.DenormalizedRtpbiRequestDto
import me.dmadouros.eda.direct.events.RtpbiRequestReceived
import me.dmadouros.eda.direct.test.RtpbiRequestFactory
import me.dmadouros.eda.pharmacy.commands.IdentifyPharmacy
import me.dmadouros.eda.pharmacy.events.PharmacyFound
import me.dmadouros.eda.shared.infrastructure.MessageStore
import me.dmadouros.eda.shared.infrastructure.PharmacyRepository

object Projection {
    var count: Int = 0

    fun updateCount() = count++
}

fun Application.configureDirect(
    messageStore: MessageStore,
    objectMapper: ObjectMapper,
    pharmacyRepository: PharmacyRepository
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
        if (it.originalEvent.eventType == "RtpbiRequestReceived") {
            val rtpbiRequestReceived: RtpbiRequestReceived = objectMapper.readValue(it.originalEvent.eventData)
            val pharmacyIdentified = IdentifyPharmacy(pharmacyRepository).call(
                rtpbiRequestReceived.id,
                rtpbiRequestReceived.body.prescription.pharmacy.npi
            )
            messageStore.writeEvent(pharmacyIdentified)
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
                    ndcCode = rtpbiRequest.prescription.ndcCode,
                    prescriberNpi = rtpbiRequest.prescription.prescriberNpi,
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

    val projection = mapOf(
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
