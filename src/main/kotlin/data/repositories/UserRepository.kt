package app.data.repositories

import app.data.tables.UsersTable
import app.domain.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun insert(req: UserCreateRequest): UserResponse = transaction {
        val id = UsersTable.insertAndGetId {
            it[name] = req.name; it[email] = req.email; it[phone] = req.phone
        }.value
        UserResponse(id, req.name, req.email, req.phone)
    }

    fun list(): List<UserResponse> = transaction {
        UsersTable.selectAll().map {
            UserResponse(it[UsersTable.id].value, it[UsersTable.name], it[UsersTable.email], it[UsersTable.phone])
        }
    }

    fun findByEmail(email: String): UserResponse? = transaction {
        UsersTable.select { UsersTable.email eq email }.singleOrNull()?.let {
            UserResponse(it[UsersTable.id].value, it[UsersTable.name], it[UsersTable.email], it[UsersTable.phone])
        }
    }
}
