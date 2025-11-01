package com.payir.controllers

import com.payir.dto.*
import com.payir.helpers.requireAuth
import com.payir.services.FacilityService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class FacilityController {
    private val facilityService = FacilityService()
    
    fun Route.getFacilitiesRoute() {
        get("/api/v1/facilities") {
            try {
                val district = call.request.queryParameters["district"]
                val taluk = call.request.queryParameters["taluk"]
                val village = call.request.queryParameters["village"]
                val type = call.request.queryParameters["type"]
                
                val facilities = facilityService.getFacilities(district, taluk, village, type)
                call.respond(facilities)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get facilities: ${e.message}"))
            }
        }
    }
    
    fun Route.getFacilityRoute() {
        get("/api/v1/facilities/{id}") {
            try {
                val facilityId = call.parameters["id"] ?: throw IllegalArgumentException("Facility ID required")
                val facility = facilityService.getFacilityById(facilityId)
                    ?: throw IllegalArgumentException("Facility not found")
                call.respond(facility)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get facility: ${e.message}"))
            }
        }
    }
    
    fun Route.createFacilityRoute() {
        authenticate("basicAuth") {
            post("/api/v1/facilities") {
                try {
                    val user = call.requireAuth()
                    if (user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required"))
                        return@post
                    }
                    
                    val request = call.receive<CreateFacilityRequest>()
                    val response = facilityService.createFacility(request, user.userId)
                    call.respond(HttpStatusCode.Created, response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to create facility: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.updateFacilityRoute() {
        authenticate("basicAuth") {
            put("/api/v1/facilities/{id}") {
                try {
                    val facilityId = call.parameters["id"] ?: throw IllegalArgumentException("Facility ID required")
                    val user = call.requireAuth()
                    if (user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required"))
                        return@put
                    }
                    
                    val request = call.receive<UpdateFacilityRequest>()
                    val response = facilityService.updateFacility(facilityId, request, user.userId)
                    call.respond(response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to update facility: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getFacilityHistoryRoute() {
        authenticate("basicAuth") {
            get("/api/v1/facilities/{id}/history") {
                try {
                    val facilityId = call.parameters["id"] ?: throw IllegalArgumentException("Facility ID required")
                    val user = call.requireAuth()
                    if (user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required"))
                        return@get
                    }
                    
                    val history = facilityService.getFacilityHistory(facilityId)
                    call.respond(history)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get facility history: ${e.message}"))
                }
            }
        }
    }
}
