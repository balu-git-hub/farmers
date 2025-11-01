package com.payir.controllers

import com.payir.dto.*
import com.payir.helpers.requireAuth
import com.payir.services.AgriDeptService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AgriDeptController {
    private val agriDeptService = AgriDeptService()
    
    fun Route.getPendingFarmersRoute() {
        authenticate("basicAuth") {
            get("/api/v1/agri/farmers/pending") {
                try {
                    val user = call.requireAuth()
                    if (user.role != "AGRI_DEPT") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Agri Dept access required"))
                        return@get
                    }
                    
                    val farmers = agriDeptService.getPendingFarmers()
                    call.respond(farmers)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get pending farmers: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.approveFarmerRoute() {
        authenticate("basicAuth") {
            post("/api/v1/agri/farmers/{id}/approve") {
                try {
                    val farmerId = call.parameters["id"] ?: throw IllegalArgumentException("Farmer ID required")
                    val user = call.requireAuth()
                    if (user.role != "AGRI_DEPT") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Agri Dept access required"))
                        return@post
                    }
                    
                    // Get agri dept personnel ID from user - would need to query agri_dept_personnel table
                    // For now, using userId as personnel ID
                    val response = agriDeptService.approveFarmer(farmerId, user.userId)
                    call.respond(response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to approve farmer: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.rejectFarmerRoute() {
        authenticate("basicAuth") {
            post("/api/v1/agri/farmers/{id}/reject") {
                try {
                    val farmerId = call.parameters["id"] ?: throw IllegalArgumentException("Farmer ID required")
                    val user = call.requireAuth()
                    if (user.role != "AGRI_DEPT") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Agri Dept access required"))
                        return@post
                    }
                    
                    val request = call.receive<FarmerApprovalRequest>()
                    val response = agriDeptService.rejectFarmer(
                        farmerId = farmerId,
                        rejectionReason = request.rejectionReason ?: "Rejected by Agri Dept",
                        agriDeptPersonnelId = user.userId
                    )
                    call.respond(response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to reject farmer: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.getPendingDocumentsRoute() {
        authenticate("basicAuth") {
            get("/api/v1/agri/documents/pending") {
                try {
                    val user = call.requireAuth()
                    if (user.role != "AGRI_DEPT") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Agri Dept access required"))
                        return@get
                    }
                    
                    val documents = agriDeptService.getPendingDocuments()
                    call.respond(documents)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get pending documents: ${e.message}"))
                }
            }
        }
    }
    
    fun Route.verifyDocumentRoute() {
        authenticate("basicAuth") {
            post("/api/v1/agri/documents/{id}/verify") {
                try {
                    val documentId = call.parameters["id"] ?: throw IllegalArgumentException("Document ID required")
                    val user = call.requireAuth()
                    if (user.role != "AGRI_DEPT") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Agri Dept access required"))
                        return@post
                    }
                    
                    val request = call.receive<DocumentVerificationRequest>()
                    val response = agriDeptService.verifyDocument(
                        documentId = documentId,
                        request = request,
                        agriDeptPersonnelId = user.userId
                    )
                    call.respond(response)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to verify document: ${e.message}"))
                }
            }
        }
    }
}
