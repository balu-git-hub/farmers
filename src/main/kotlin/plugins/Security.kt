package com.payir.plugins

import com.payir.helpers.AuthHelper
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    install(Authentication) {
        basic(name = "basicAuth") {
            realm = "Payir API"
            validate { credentials ->
                AuthHelper.validate(credentials)
            }
        }
    }
}

