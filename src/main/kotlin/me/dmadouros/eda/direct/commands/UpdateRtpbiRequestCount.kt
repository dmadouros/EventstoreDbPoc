package me.dmadouros.eda.direct.commands

import me.dmadouros.eda.direct.Projection

object UpdateRtpbiRequestCount {
    fun call() {
        Projection.updateCount()
    }
}
