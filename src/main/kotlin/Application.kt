package com.kug

import io.ktor.server.application.*
import com.payir.database.DatabaseFactory
import com.payir.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 3000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)}

fun Application.module() {
    DatabaseFactory.init()
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureRouting()
}
