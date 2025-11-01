package com.payir.dto

import kotlinx.serialization.Serializable

// Auth Request DTOs
@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String, // FARMER, ADMIN, AGRI_DEPT
    val email: String? = null,
    val phone: String? = null
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

// Auth Response DTOs
@Serializable
data class AuthResponse(
    val userId: String,
    val username: String,
    val role: String,
    val token: String? = null
)

@Serializable
data class RegisterResponse(
    val userId: String,
    val username: String,
    val role: String
)

@Serializable
data class MessageResponse(
    val message: String
)

