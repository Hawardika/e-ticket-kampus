package app.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val name: String,
    val email: String,
    val phone: String? = null
)

@Serializable
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?
)