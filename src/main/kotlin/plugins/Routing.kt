package com.payir.plugins

import com.payir.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Health check
        get("/") {
            call.respondText("🌾 Payir Farmers API is running successfully!")
        }
        
        // API Routes
        authRoutes()
        farmerRoutes()
        agriDeptRoutes()
        facilityRoutes()
        bookingRoutes()
        storageRecordRoutes()
        locationRoutes()
        syncRoutes()
        adminRoutes()
    }
}

