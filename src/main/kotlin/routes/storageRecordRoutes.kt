package com.payir.routes

import com.payir.controllers.StorageRecordController
import io.ktor.server.routing.*

fun Route.storageRecordRoutes() {
    val storageRecordController = StorageRecordController()
    
    with(storageRecordController) {
        createStorageRecordRoute()
        getStorageRecordsRoute()
        getStorageRecordHistoryRoute()
    }
}
