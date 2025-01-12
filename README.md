# ktor-server
Learning repo for kotlin/ktor.

Implements a TODO API

### Endpoints

 `GET /todos`<br>
 Returns a list of all todos.


 `GET /todos/{id}`<br>
 Fetches a specific todo by its ID.
 404 if the todo is not found.


 `POST /todos`<br>
 Accepts a JSON body with title, description (optional), and isDone.
 Returns the created todo.


 `PUT /todos/{id}`<br>
 Updates a todo by its ID using the provided JSON body.
 404 if the todo is not found.


 `DELETE /todos/{id}`
 Deletes a todo by its ID.
 204 on success, 404 if not found.

### Implementation details

- Postgres Database
- Flyway for migrations 
- JOOQ as an ORM
- ktor HTTP, Routing
- kotlinx for serialization

Requires a local PostgreSQL server:
```
Port: 5432
Database: postgres
Username: ktor_user
Password: secretpw
```


