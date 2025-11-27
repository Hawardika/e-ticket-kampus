package app.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class EventCreateRequest(val title: String, val organizer: String, val date: String, val venue: String)
@Serializable
data class EventResponse(
    val id: Long,
    val title: String,
    val organizer: String,
    val date: String,
    val venue: String,
    val status: String
)
