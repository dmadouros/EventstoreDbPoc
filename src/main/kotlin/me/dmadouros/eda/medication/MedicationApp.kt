package me.dmadouros.eda.medication

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.dmadouros.eda.medication.commands.AddMedication
import me.dmadouros.eda.medication.dtos.MedicationDto
import me.dmadouros.eda.shared.infrastructure.MessageStore

fun Application.configureMedication(
    messageStore: MessageStore,
) {
    routing {
        post("/medication/medications") {
            val medication = call.receive<MedicationDto>()
            AddMedication(messageStore).call(medication)
            call.respond(HttpStatusCode.Created)
        }
    }
}
