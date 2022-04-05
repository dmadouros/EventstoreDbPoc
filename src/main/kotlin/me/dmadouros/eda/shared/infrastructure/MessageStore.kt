package me.dmadouros.eda.shared.infrastructure

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.Position
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.RecordedEvent
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscribeToAllOptions
import com.eventstore.dbclient.Subscription
import com.eventstore.dbclient.SubscriptionFilter
import com.eventstore.dbclient.SubscriptionListener
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.dmadouros.eda.shared.events.Event
import java.util.concurrent.ExecutionException

private data class PositionEvent(override val id: String, val position: Long) : Event {
    override val type: String = "Read"
    override val category: String = "subscriberPosition"
}

class MessageStore(val client: EventStoreDBClient, val objectMapper: ObjectMapper) {
    fun writeEvent(event: Event) {
        val eventData = EventData
            .builderAsJson(event.type, event)
            .build()

        client.appendToStream("${event.category}-${event.id}", eventData).get()
    }

    fun readEvents(category: String, id: String): List<RecordedEvent> {
        val options = ReadStreamOptions.get()
            .forwards()
            .fromStart()

        return client.readStream("$category-$id", 10, options).get()
            .events
            .map { it.originalEvent }
    }

    fun subscribe(
        category: String,
        subscriberId: String,
        fromStart: Boolean = false,
        eventHandler: (ResolvedEvent) -> Unit
    ) {
        val subscriberStreamName = "subscriberPosition-$subscriberId"

        val listener: SubscriptionListener = object : SubscriptionListener() {
            override fun onEvent(subscription: Subscription, event: ResolvedEvent) {
                eventHandler(event)
                writeEvent(PositionEvent(id = subscriberId, position = event.originalEvent.position.commitUnsigned))
            }
        }

        val position = if (fromStart) 0 else loadPosition(subscriberStreamName)

        val filter = SubscriptionFilter.newBuilder()
            .withStreamNamePrefix("$category-")
            .build()
        val options = SubscribeToAllOptions.get().filter(filter).fromPosition(Position(position, position))
        client.subscribeToAll(listener, options)
    }

    private fun loadPosition(subscriberStreamName: String): Long {
        return try {
            readLastMessage(subscriberStreamName)?.position ?: 0
        } catch (e: ExecutionException) {
            0
        }
    }

    private fun readLastMessage(subscriberStreamName: String): PositionEvent? {
        val options = ReadStreamOptions.get()
            .backwards()
            .fromEnd()

        return client.readStream(subscriberStreamName, 1, options).get()
            .events
            .firstOrNull()?.let { objectMapper.readValue<PositionEvent>(it.originalEvent.eventData) }
    }
}
