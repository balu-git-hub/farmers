package com.payir.routes

import com.payir.controllers.SyncController
import io.ktor.server.routing.*

fun Route.syncRoutes() {
    val syncController = SyncController()
    
    with(syncController) {
        getSeedPackageRoute()
        syncChangesRoute()
    }
}
