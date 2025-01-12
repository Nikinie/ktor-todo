package com.nikinie

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val todoRepository = TodoRepository(DatabaseFactory.dslContext)

    routing {
        todoRoutes(todoRepository)
    }
}

