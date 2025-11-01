package com.payir.routes

import com.payir.controllers.BookingController
import io.ktor.server.routing.*

fun Route.bookingRoutes() {
    val bookingController = BookingController()
    
    with(bookingController) {
        checkAvailabilityRoute()
        createBookingRoute()
        getBookingsRoute()
        getBookingRoute()
        getBookingHistoryRoute()
        cancelBookingRoute()
    }
}
