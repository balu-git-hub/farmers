package com.payir.controllers

import com.payir.dto.*
import com.payir.helpers.requireAuth
import com.payir.services.AdminService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AdminController {
    private val adminService = AdminService()
    
    fun Route.getAllBookingsRoute() {
        authenticate("basicAuth") {
            get("/api/v1/admin/bookings") {
                try {
                    val user = call.requireAuth()
                    if (user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required"))
                        return@get
                    }
                    
                    val bookings = adminService.getAllBookings()
                    call.respond(bookings)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get bookings: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getAllStorageRecordsRoute() {
        authenticate("basicAuth") {
            get("/api/v1/admin/storage-records") {
                try {
                    val user = call.requireAuth()
                    if (user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required"))
                        return@get
                    }
                    
                    val records = adminService.getAllStorageRecords()
                    call.respond(records)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get storage records: ${e.message}"))
                }
            }
        }
    }
}
