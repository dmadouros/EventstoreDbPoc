package me.dmadouros.eda.quom.commands

import me.dmadouros.eda.quom.events.QuomFound
import me.dmadouros.eda.quom.events.QuomIdentified
import me.dmadouros.eda.shared.infrastructure.QuomRepository

class IdentifyQuom(private val quomRepository: QuomRepository) {
    fun call(id: String, ncitCode: String): QuomIdentified {
        val quom = quomRepository.findByNcitCode(ncitCode)

        return QuomFound(id, quom)
    }
}
