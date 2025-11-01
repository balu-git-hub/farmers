package com.payir.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.HttpMethod

fun Application.configureHTTP() {
    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }
}

