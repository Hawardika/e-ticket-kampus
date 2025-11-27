package app.domain.services

import app.data.repositories.UserRepository
import app.domain.models.*
import app.plugins.ValidationException
import app.plugins.ConflictException

class UserService(private val repo: UserRepository = UserRepository()) {
    fun create(req: UserCreateRequest): UserResponse {
        val errs = mutableMapOf<String, String>()
        if (req.name.isBlank()) errs["name"] = "Harus diisi"
        if (req.email.isBlank()) errs["email"] = "Harus diisi"
        if (errs.isNotEmpty()) throw ValidationException(errs)
        if (repo.findByEmail(req.email) != null) throw ConflictException("Email sudah terdaftar")
        return repo.insert(req)
    }

    fun list(): List<UserResponse> = repo.list()
}
