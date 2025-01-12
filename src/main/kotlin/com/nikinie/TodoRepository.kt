package com.nikinie

import com.nikinie.jooq.tables.Todo
import kotlinx.serialization.Serializable
import org.jooq.DSLContext

@Serializable
data class TodoD(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val isDone: Boolean
)

class TodoRepository(private val dslContext: DSLContext) {

    fun create(title: String, description: String?, isDone: Boolean = false): TodoD {
        val inserted = dslContext.insertInto(Todo.TODO)
            .set(Todo.TODO.TITLE, title)
            .set(Todo.TODO.DESCRIPTION, description)
            .set(Todo.TODO.IS_DONE, isDone)
            .returning()
            .fetchOne()!!
        return TodoD(inserted.id, inserted.title, inserted.description, isDone)
    }

    fun findById(id: Int): TodoD? {
        val found = dslContext.selectFrom(Todo.TODO)
            .where(Todo.TODO.ID.eq(id))
            .fetchOne()
        if (found != null) {
            return TodoD(found.id, found.title, found.description, found.isDone)
        }
        return null
    }

    fun findAll(): List<TodoD> {
        val all = dslContext.selectFrom(Todo.TODO)
            .fetch()
        return all.map { TodoD(it.id, it.title, it.description, it.isDone) }
    }

    fun update(id: Int, title: String?, description: String?, isDone: Boolean?): TodoD? {
        val updated = dslContext.update(Todo.TODO)
            .set(Todo.TODO.TITLE, title)
            .set(Todo.TODO.DESCRIPTION, description)
            .set(Todo.TODO.IS_DONE, isDone)
            .where(Todo.TODO.ID.eq(id))
            .returning()
            .fetchOne()
        if (updated != null) {
            return TodoD(updated.id, updated.title, updated.description, updated.isDone)
        }
        return null
    }

    fun delete(id: Int): Boolean {
        return dslContext.deleteFrom(Todo.TODO)
            .where(Todo.TODO.ID.eq(id))
            .execute() > 0
    }

    fun findByTitle(title: String): List<TodoD> {
        val allFound = dslContext.selectFrom(Todo.TODO)
            .where(Todo.TODO.TITLE.like("%$title%"))
            .fetch()
        return allFound.map { TodoD(it.id, it.title, it.description, it.isDone) }
    }

    fun findByIsDone(isDone: Boolean): List<TodoD> {
        val allFound = dslContext.selectFrom(Todo.TODO)
            .where(Todo.TODO.IS_DONE.eq(isDone))
            .fetch()
        return allFound.map { TodoD(it.id, it.title, it.description, it.isDone) }
    }
}
