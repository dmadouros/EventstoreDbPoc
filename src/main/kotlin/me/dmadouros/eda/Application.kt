package me.dmadouros.eda

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.ContentNegotiation
import me.dmadouros.eda.direct.configureDirect
import me.dmadouros.eda.shared.infrastructure.MessageStore
import me.dmadouros.eda.shared.infrastructure.PharmacyRepository
import me.dmadouros.eda.pharmacy.configurePharmacy

fun main() {
    val connectionString = "esdb://admin:changeit@localhost:2113?tls=false"
    val settings: EventStoreDBClientSettings = EventStoreDBConnectionString.parse(connectionString)
    val client: EventStoreDBClient = EventStoreDBClient.create(settings)
    val objectMapper = ObjectMapper().registerModule(KotlinModule())
    val messageStore = MessageStore(client, objectMapper)
    val pharmacyRepository = PharmacyRepository(messageStore)

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureApplication(messageStore, objectMapper, pharmacyRepository)
    }.start(wait = true)
}

fun Application.configureApplication(
    messageStore: MessageStore,
    objectMapper: ObjectMapper,
    pharmacyRepository: PharmacyRepository
) {
    install(ContentNegotiation) {
        jackson()
    }

    configureDirect(messageStore, objectMapper, pharmacyRepository)
    configurePharmacy(messageStore)
}
