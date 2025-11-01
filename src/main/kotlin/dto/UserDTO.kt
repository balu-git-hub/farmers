package com.payir.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val role: String,
    val email: String?,
    val phone: String?,
    val isActive: Boolean
)

