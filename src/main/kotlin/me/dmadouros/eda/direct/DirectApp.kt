package me.dmadouros.eda.direct

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import me.dmadouros.eda.direct.commands.NormalizeRtpbiRequest
import me.dmadouros.eda.direct.commands.UpdateRtpbiRequestCount
import me.dmadouros.eda.direct.events.RtpbiRequestReceived
import me.dmadouros.eda.direct.test.RtpbiRequestFactory
import me.dmadouros.eda.shared.infrastructure.MessageStore
import me.dmadouros.eda.shared.infrastructure.PharmacyRepository
import java.util.UUID

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
        subscriberId = "components:normalizeRtpbiRequest",
    ) {
        if (it.originalEvent.eventType == "RtpbiRequestReceived") {
            try {
                val rtpbiRequestReceived: RtpbiRequestReceived = objectMapper.readValue(it.originalEvent.eventData)
                val rtpbiRequestNormalized = NormalizeRtpbiRequest(pharmacyRepository).call(rtpbiRequestReceived)
                messageStore.writeEvent(rtpbiRequestNormalized)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    routing {
        get("/rtpbiRequestCount") {
            mapOf("count" to Projection.count)
            call.respond(mapOf("count" to Projection.count))
        }

        get("/receiveRtpbiRequest") {
            val rtpbiRequest = RtpbiRequestFactory.create()
            val event = RtpbiRequestReceived(UUID.randomUUID().toString(), rtpbiRequest)
            messageStore.writeEvent(event)

            call.response.status(HttpStatusCode.OK)
        }
    }
}
