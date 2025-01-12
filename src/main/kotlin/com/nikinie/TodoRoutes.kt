// TodoRoutes.kt
package com.nikinie

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class TodoDTO(val title: String, val description: String?, val isDone: Boolean)

fun Route.todoRoutes(todoRepository: TodoRepository) {
    route("/todos") {
        get {
            call.respond(todoRepository.findAll())
        }

        get("/{id}") {
            val id =
                call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            val todo = todoRepository.findById(id) ?: return@get call.respond(HttpStatusCode.NotFound, "Todo not found")
            call.respond(todo)
        }

        post {
            val todoDTO = call.receive<TodoDTO>()
            val newTodo = todoRepository.create(todoDTO.title, todoDTO.description, todoDTO.isDone)
            call.respond(HttpStatusCode.Created, newTodo)
        }

        put("/{id}") {
            val id =
                call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            val todoDTO = call.receive<TodoDTO>()
            val updatedTodo = todoRepository.update(id, todoDTO.title, todoDTO.description, todoDTO.isDone)
            if (updatedTodo != null) {
                call.respond(updatedTodo)
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo not found")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Invalid ID"
            )
            val deleted = todoRepository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo not found")
            }
        }
    }
}
