package com.payir.routes

import com.payir.controllers.AgriDeptController
import io.ktor.server.routing.*

fun Route.agriDeptRoutes() {
    val agriDeptController = AgriDeptController()
    
    with(agriDeptController) {
        getPendingFarmersRoute()
        approveFarmerRoute()
        rejectFarmerRoute()
        getPendingDocumentsRoute()
        verifyDocumentRoute()
    }
}
