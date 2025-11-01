package com.payir.routes

import com.payir.controllers.LocationController
import io.ktor.server.routing.*

fun Route.locationRoutes() {
    val locationController = LocationController()
    
    with(locationController) {
        getAllDistrictsRoute()
        getTaluksByDistrictRoute()
        getVillagesByTalukRoute()
    }
}
