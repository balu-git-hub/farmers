package com.payir.routes

import com.payir.controllers.AuthController
import io.ktor.server.routing.*

fun Route.authRoutes() {
    val authController = AuthController()
    
    with(authController) {
        registerRoute()
        loginRoute()
        logoutRoute()
    }
}
