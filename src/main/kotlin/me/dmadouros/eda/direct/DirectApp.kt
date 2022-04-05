package me.dmadouros.eda.direct

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import me.dmadouros.eda.direct.commands.UpdateRtpbiRequestCount
import me.dmadouros.eda.direct.events.RtpbiRequestReceived
import me.dmadouros.eda.direct.test.RtpbiRequestFactory
import me.dmadouros.eda.medication.commands.IdentifyMedication
import me.dmadouros.eda.pharmacy.commands.IdentifyPharmacy
import me.dmadouros.eda.provider.commands.IdentifyProvider
import me.dmadouros.eda.shared.infrastructure.MedicationRepository
import me.dmadouros.eda.shared.infrastructure.MessageStore
import me.dmadouros.eda.shared.infrastructure.PharmacyRepository
import me.dmadouros.eda.shared.infrastructure.ProviderRepository
import java.util.UUID

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

        get("/direct/receiveRtpbiRequest") {
            RtpbiRequestReceived(UUID.randomUUID().toString(), RtpbiRequestFactory.create())
                .let { event -> messageStore.writeEvent(event) }
                .also { call.respond(HttpStatusCode.OK) }
        }
    }
}
