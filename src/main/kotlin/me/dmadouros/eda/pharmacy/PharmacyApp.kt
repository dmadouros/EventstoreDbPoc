package me.dmadouros.eda.pharmacy

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.dmadouros.eda.shared.infrastructure.MessageStore
import me.dmadouros.eda.pharmacy.commands.AddPharmacy
import me.dmadouros.eda.pharmacy.dtos.PharmacyDto

fun Application.configurePharmacy(messageStore: MessageStore) {
    routing {
        post("/pharmacies") {
            val pharmacy = call.receive<PharmacyDto>()

            val pharmacyAdded = AddPharmacy.call(pharmacy)
            messageStore.writeEvent(pharmacyAdded)

            call.response.status(HttpStatusCode.Created)
        }
    }
}
