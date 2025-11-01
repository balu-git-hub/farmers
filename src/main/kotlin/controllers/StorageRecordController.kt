package com.payir.controllers

import com.payir.dto.*
import com.payir.helpers.requireAuth
import com.payir.services.FarmerService
import com.payir.services.StorageRecordService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class StorageRecordController {
    private val storageRecordService = StorageRecordService()
    private val farmerService = FarmerService()
    
    fun Route.createStorageRecordRoute() {
        authenticate("basicAuth") {
            post("/api/v1/storage-records") {
                try {
                    val request = call.receive<CreateStorageRecordRequest>()
                    val user = call.requireAuth()
                    
                    // Get farmer ID from user
                    val farmer = farmerService.getFarmerByUserId(user.userId)
                        ?: throw IllegalArgumentException("Farmer profile not found. Please complete registration.")
                    
                    val response = storageRecordService.createStorageRecord(
                        farmerId = farmer.id,
                        request = request,
                        createdBy = user.userId
                    )
                    call.respond(HttpStatusCode.Created, response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to create storage record: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getStorageRecordsRoute() {
        authenticate("basicAuth") {
            get("/api/v1/storage-records") {
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
                    
                    val records = storageRecordService.getStorageRecordsByFarmer(farmerId)
                    call.respond(records)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get storage records: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getStorageRecordHistoryRoute() {
        authenticate("basicAuth") {
            get("/api/v1/storage-records/{id}/history") {
                try {
                    val recordId = call.parameters["id"] ?: throw IllegalArgumentException("Record ID required")
                    val user = call.requireAuth()
                    
                    val record = storageRecordService.getStorageRecordById(recordId)
                        ?: throw IllegalArgumentException("Storage record not found")
                    
                    // Check access
                    val farmer = farmerService.getFarmerByUserId(user.userId)
                    if (record.farmerId != farmer?.id && user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@get
                    }
                    
                    val history = storageRecordService.getStorageRecordHistory(recordId)
                    call.respond(history)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get storage record history: ${e.message}"))
                }
            }
        }
    }
}
