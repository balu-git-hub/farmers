package com.payir.routes

import com.payir.controllers.AdminController
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    val adminController = AdminController()
    
    with(adminController) {
        getAllBookingsRoute()
        getAllStorageRecordsRoute()
    }
}
