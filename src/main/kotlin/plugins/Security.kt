package com.payir.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    install(Authentication) {
        basic(name = "basicAuth") {
            realm = "Payir API"
            validate { credentials ->
                // TODO: Implement AuthHelper.validate() in helpers/AuthHelper.kt
                null
            }
        }
    }
}

