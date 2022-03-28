package me.dmadouros.eda.direct.events

import me.dmadouros.eda.direct.dtos.RtpbiRequestDto
import me.dmadouros.eda.shared.events.Event

data class RtpbiRequestReceived(override val id: String, val body: RtpbiRequestDto) : Event {
    override val category = "rtpbiRequest"
    override val type = "RtpbiRequestReceived"
}
