package com.payir.controllers

import com.payir.dto.*
import com.payir.services.LocationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class LocationController {
    private val locationService = LocationService()
    
    fun Route.getAllDistrictsRoute() {
        get("/api/v1/locations/districts") {
            try {
                val districts = locationService.getAllDistricts()
                call.respond(districts)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get districts: ${e.message}"))
            }
        }
    }
    
    fun Route.getTaluksByDistrictRoute() {
        get("/api/v1/locations/{district}/taluks") {
            try {
                val districtName = call.parameters["district"] ?: throw IllegalArgumentException("District name required")
                val taluks = locationService.getTaluksByDistrictName(districtName)
                call.respond(taluks)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get taluks: ${e.message}"))
            }
        }
    }
    
    fun Route.getVillagesByTalukRoute() {
        get("/api/v1/locations/{taluk}/villages") {
            try {
                val talukName = call.parameters["taluk"] ?: throw IllegalArgumentException("Taluk name required")
                val villages = locationService.getVillagesByTalukName(talukName)
                call.respond(villages)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get villages: ${e.message}"))
            }
        }
    }
}
