package me.dmadouros.eda.provider

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.dmadouros.eda.provider.commands.AddProvider
import me.dmadouros.eda.provider.dtos.ProviderDto
import me.dmadouros.eda.shared.infrastructure.MessageStore

fun Application.configureProvider(messageStore: MessageStore) {
    routing {
        post("/provider/providers") {
            val provider = call.receive<ProviderDto>()

            AddProvider(messageStore).call(provider)

            call.response.status(HttpStatusCode.Created)
        }
    }
}
