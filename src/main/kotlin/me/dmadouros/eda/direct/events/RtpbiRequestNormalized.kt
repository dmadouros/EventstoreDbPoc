package me.dmadouros.eda.direct.events

import me.dmadouros.eda.direct.dtos.NormalizedRtpbiRequestDto
import me.dmadouros.eda.shared.events.Event

data class RtpbiRequestNormalized(override val id: String, val body: NormalizedRtpbiRequestDto) : Event {
    override val type: String = "RtpbiRequestNormalized"
    override val category: String = "rtpbiRequest"
}
