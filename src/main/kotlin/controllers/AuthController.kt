package com.payir.controllers

import com.payir.dto.*
import com.payir.services.AuthService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AuthController {
    private val authService = AuthService()

    fun Route.registerRoute() {
        post("/api/v1/auth/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val response = authService.register(request)
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        MessageResponse(e.message ?: "Invalid request")
                )
            } catch (e: Exception) {
                call.respond(
                        io.ktor.http.HttpStatusCode.InternalServerError,
                        MessageResponse("Registration failed: ${e.message}")
                )
            }
        }
    }

    fun Route.loginRoute() {
        post("/api/v1/auth/login") {
            try {
                val request = call.receive<LoginRequest>()
                val response = authService.login(request)
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(
                        io.ktor.http.HttpStatusCode.Unauthorized,
                        MessageResponse(e.message ?: "Invalid credentials")
                )
            } catch (e: Exception) {
                call.respond(
                        io.ktor.http.HttpStatusCode.InternalServerError,
                        MessageResponse("Login failed: ${e.message}")
                )
            }
        }
    }

    fun Route.logoutRoute() {
        authenticate("basicAuth") {
            post("/api/v1/auth/logout") { call.respond(MessageResponse("Logged out successfully")) }
        }
    }
}
