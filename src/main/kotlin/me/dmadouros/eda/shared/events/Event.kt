package me.dmadouros.eda.shared.events

interface Event {
    val id: String
    val category: String
    val type: String
}
