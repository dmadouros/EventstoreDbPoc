package me.dmadouros.eda.quom.commands

import me.dmadouros.eda.quom.dtos.QuomDto
import me.dmadouros.eda.quom.events.QuomAdded
import me.dmadouros.eda.shared.infrastructure.MessageStore

class AddQuom(private val messageStore: MessageStore) {
    fun call(quom: QuomDto) {
        val quomAdded = QuomAdded(id = quom.ncitCode, body = quom)
        messageStore.writeEvent(quomAdded)
    }
}
