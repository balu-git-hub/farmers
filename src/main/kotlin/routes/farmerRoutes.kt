package com.payir.routes

import com.payir.controllers.FarmerController
import io.ktor.server.routing.*

fun Route.farmerRoutes() {
    val farmerController = FarmerController()
    
    with(farmerController) {
        registerRoute()
        getFarmerRoute()
        getCropPlantingsRoute()
        createCropPlantingRoute()
        uploadDocumentRoute()
    }
}
