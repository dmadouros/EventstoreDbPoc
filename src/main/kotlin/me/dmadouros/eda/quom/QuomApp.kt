package me.dmadouros.eda.quom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.dmadouros.eda.quom.commands.AddQuom
import me.dmadouros.eda.quom.dtos.QuomDto
import me.dmadouros.eda.shared.infrastructure.MessageStore

fun Application.configureQuom(
    messageStore: MessageStore,
) {
    routing {
        post("/quom/quoms") {
            val quom = call.receive<QuomDto>()
            AddQuom(messageStore).call(quom)
            call.respond(HttpStatusCode.Created)
        }
    }
}
