package com.payir.controllers

import com.payir.dto.*
import com.payir.helpers.getCurrentUser
import com.payir.helpers.requireAuth
import com.payir.services.FarmerService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

class FarmerController {
    private val farmerService = FarmerService()
    
    fun Route.registerRoute() {
        post("/api/v1/farmers/register") {
            try {
                val request = call.receive<FarmerRegisterRequest>()
                val response = farmerService.registerFarmer(request)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Registration failed: ${e.message}"))
            }
        }
    }
    
    fun Route.getFarmerRoute() {
        authenticate("basicAuth") {
            get("/api/v1/farmers/{id}") {
                try {
                    val farmerId = call.parameters["id"] ?: throw IllegalArgumentException("Farmer ID required")
                    val user = call.requireAuth()
                    
                    // Check if user has access (own farmer record or admin/agri_dept)
                    val farmer = farmerService.getFarmerById(farmerId)
                        ?: throw IllegalArgumentException("Farmer not found")
                    
                    if (farmer.userId != user.userId && user.role != "ADMIN" && user.role != "AGRI_DEPT") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@get
                    }
                    
                    call.respond(farmer)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get farmer: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getCropPlantingsRoute() {
        authenticate("basicAuth") {
            get("/api/v1/farmers/{id}/crop-plantings") {
                try {
                    val farmerId = call.parameters["id"] ?: throw IllegalArgumentException("Farmer ID required")
                    val user = call.requireAuth()
                    
                    // Check access
                    val farmer = farmerService.getFarmerById(farmerId)
                        ?: throw IllegalArgumentException("Farmer not found")
                    
                    if (farmer.userId != user.userId && user.role != "ADMIN" && user.role != "AGRI_DEPT") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@get
                    }
                    
                    val plantings = farmerService.getCropPlantings(farmerId)
                    call.respond(plantings)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get crop plantings: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.createCropPlantingRoute() {
        authenticate("basicAuth") {
            post("/api/v1/farmers/{id}/crop-plantings") {
                try {
                    val farmerId = call.parameters["id"] ?: throw IllegalArgumentException("Farmer ID required")
                    val request = call.receive<CropPlantingRequest>()
                    val user = call.requireAuth()
                    
                    // Check access
                    val farmer = farmerService.getFarmerById(farmerId)
                        ?: throw IllegalArgumentException("Farmer not found")
                    
                    if (farmer.userId != user.userId && user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@post
                    }
                    
                    val response = farmerService.createCropPlanting(farmerId, request, user.userId)
                    call.respond(HttpStatusCode.Created, response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to create crop planting: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.uploadDocumentRoute() {
        authenticate("basicAuth") {
            post("/api/v1/farmers/{id}/documents/upload") {
                try {
                    val farmerId = call.parameters["id"] ?: throw IllegalArgumentException("Farmer ID required")
                    val user = call.requireAuth()
                    
                    // Check access
                    val farmer = farmerService.getFarmerById(farmerId)
                        ?: throw IllegalArgumentException("Farmer not found")
                    
                    if (farmer.userId != user.userId && user.role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Access denied"))
                        return@post
                    }
                    
                    val multipart = call.receiveMultipart()
                    var documentType = ""
                    var documentName = ""
                    var filePath = ""
                    var fileSizeBytes: Long? = null
                    var mimeType: String? = null
                    
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "documentType" -> {
                                        documentType = part.value
                                    }
                                    "documentName" -> {
                                        documentName = part.value
                                    }
                                }
                                part.dispose()
                            }
                            is PartData.FileItem -> {
                                if (part.name == "file") {
                                    val fileName = part.originalFileName ?: "upload.pdf"
                                    mimeType = part.contentType?.contentType
                                    
                                    // For MVP, save to disk (in production, use cloud storage)
                                    val uploadDir = File("uploads")
                                    if (!uploadDir.exists()) {
                                        uploadDir.mkdirs()
                                    }
                                    val file = File(uploadDir, "${java.util.UUID.randomUUID()}_$fileName")
                                    filePath = file.absolutePath
                                    val fileBytes = part.streamProvider().readBytes()
                                    fileSizeBytes = fileBytes.size.toLong()
                                    file.writeBytes(fileBytes)
                                }
                                part.dispose()
                            }
                            else -> {
                                part.dispose()
                            }
                        }
                    }
                    
                    if (documentType.isEmpty() || filePath.isEmpty()) {
                        throw IllegalArgumentException("documentType and file are required")
                    }
                    
                    val response = farmerService.uploadDocument(
                        farmerId = farmerId,
                        documentType = documentType,
                        documentName = documentName,
                        filePath = filePath,
                        fileSizeBytes = fileSizeBytes,
                        mimeType = mimeType,
                        createdBy = user.userId
                    )
                    
                    call.respond(HttpStatusCode.Created, response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to upload document: ${e.message}"))
                }
            }
        }
    }
}
