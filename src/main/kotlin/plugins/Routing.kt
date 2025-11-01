package com.payir.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
//        authRoutes()
//        farmerRoutes()
//        agriDeptRoutes()
//        facilityRoutes()
//        bookingRoutes()
//        storageRecordRoutes()
//        locationRoutes()
//        syncRoutes()
//        adminRoutes()

        get("/") {
            call.respondText("🌾 Payir Farmers API is running successfully!")
        }
    }


}

