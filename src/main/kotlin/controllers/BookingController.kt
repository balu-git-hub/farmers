package com.payir.controllers

import com.payir.dto.*
import com.payir.helpers.getCurrentUser
import com.payir.helpers.requireAuth
import com.payir.services.BookingService
import com.payir.services.FarmerService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class BookingController {
    private val bookingService = BookingService()
    private val farmerService = FarmerService()
    
    fun Route.checkAvailabilityRoute() {
        post("/api/v1/bookings/check") {
            try {
                val request = call.receive<CheckAvailabilityRequest>()
                val response = bookingService.checkAvailability(request)
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to check availability: ${e.message}"))
            }
        }
    }
    
    fun Route.createBookingRoute() {
        authenticate("basicAuth") {
            post("/api/v1/bookings") {
                try {
                    val request = call.receive<CreateBookingRequest>()
                    val user = call.requireAuth()
                    
                    // Get farmer ID from user
                    val farmer = farmerService.getFarmerByUserId(user.userId)
                        ?: throw IllegalArgumentException("Farmer profile not found. Please complete registration.")
                    
                    val response = bookingService.createBooking(
                        farmerId = farmer.id,
                        request = request,
                        createdBy = user.userId
                    )
                    call.respond(HttpStatusCode.Created, response)
                } catch (e: IllegalArgumentException) {
                    val status = if (e.message?.contains("Insufficient") == true) {
                        HttpStatusCode.Conflict
                    } else {
                        HttpStatusCode.BadRequest
                    }
                    call.respond(status, BookingConflictResponse(
                        error = "BOOKING_FAILED",
                        available = 0,
                        message = e.message ?: "Booking failed"
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to create booking: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getBookingsRoute() {
        authenticate("basicAuth") {
            get("/api/v1/bookings") {
                try {
                    val user = call.requireAuth()
                    val farmerIdParam = call.request.queryParameters["farmerId"]
                    
                    // If farmerId provided, check access; otherwise use current user's farmer ID
                    val farmer = farmerService.getFarmerByUserId(user.userId)
                        ?: throw IllegalArgumentException("Farmer profile not found")
                    
                    val farmerId = if (farmerIdParam != null) {
                        // Check if admin or own farmer
                        if (farmerIdParam != farmer.id && user.role != "ADMIN") {
                            call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                            return@get
                        }
                        farmerIdParam
                    } else {
                        farmer.id
                    }
                    
                    val bookings = bookingService.getBookingsByFarmer(farmerId)
                    call.respond(bookings)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get bookings: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getBookingRoute() {
        authenticate("basicAuth") {
            get("/api/v1/bookings/{id}") {
                try {
                    val bookingId = call.parameters["id"] ?: throw IllegalArgumentException("Booking ID required")
                    val user = call.requireAuth()
                    
                    val booking = bookingService.getBookingById(bookingId)
                        ?: throw IllegalArgumentException("Booking not found")
                    
                    // Check access - own booking or admin
                    val farmer = farmerService.getFarmerByUserId(user.userId)
                    if (booking.farmerId != farmer?.id && user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@get
                    }
                    
                    call.respond(booking)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get booking: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getBookingHistoryRoute() {
        authenticate("basicAuth") {
            get("/api/v1/bookings/{id}/history") {
                try {
                    val bookingId = call.parameters["id"] ?: throw IllegalArgumentException("Booking ID required")
                    val user = call.requireAuth()
                    
                    val booking = bookingService.getBookingById(bookingId)
                        ?: throw IllegalArgumentException("Booking not found")
                    
                    // Check access
                    val farmer = farmerService.getFarmerByUserId(user.userId)
                    if (booking.farmerId != farmer?.id && user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@get
                    }
                    
                    val history = bookingService.getBookingHistory(bookingId)
                    call.respond(history)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get booking history: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.cancelBookingRoute() {
        authenticate("basicAuth") {
            post("/api/v1/bookings/{id}/cancel") {
                try {
                    val bookingId = call.parameters["id"] ?: throw IllegalArgumentException("Booking ID required")
                    val user = call.requireAuth()
                    
                    val booking = bookingService.getBookingById(bookingId)
                        ?: throw IllegalArgumentException("Booking not found")
                    
                    // Check access - own booking or admin
                    val farmer = farmerService.getFarmerByUserId(user.userId)
                    if (booking.farmerId != farmer?.id && user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@post
                    }
                    
                    bookingService.cancelBooking(bookingId, user.userId)
                    call.respond(MessageResponse("Booking cancelled successfully"))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to cancel booking: ${e.message}"))
                }
            }
        }
    }
}
