package com.payir.routes

import com.payir.controllers.FacilityController
import io.ktor.server.routing.*

fun Route.facilityRoutes() {
    val facilityController = FacilityController()
    
    with(facilityController) {
        getFacilitiesRoute()
        getFacilityRoute()
        createFacilityRoute()
        updateFacilityRoute()
        getFacilityHistoryRoute()
    }
}
