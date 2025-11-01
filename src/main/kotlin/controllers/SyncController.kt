package com.payir.controllers

import com.payir.dto.*
import com.payir.services.SyncService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class SyncController {
    private val syncService = SyncService()
    
    fun Route.getSeedPackageRoute() {
        get("/api/v1/seed") {
            try {
                val versionParam = call.request.queryParameters["version"]
                val version = versionParam?.toIntOrNull()
                
                val seedPackage = syncService.getSeedPackage(version)
                if (seedPackage == null) {
                    // Client version matches server version - return 204
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(seedPackage)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to get seed package: ${e.message}"))
            }
        }
    }
    
    fun Route.syncChangesRoute() {
        post("/api/v1/sync/changes") {
            try {
                val request = call.receive<SyncChangesRequest>()
                
                // Get userId from request or use null if not provided
                val userId = request.userId ?: "anonymous"
                
                val response = syncService.syncChanges(userId, request)
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Invalid request"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, MessageResponse("Failed to sync changes: ${e.message}"))
            }
        }
    }
}
