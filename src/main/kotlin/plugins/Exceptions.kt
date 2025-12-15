package app.plugins

// Exception saat data tidak ditemukan (misal: ID salah)
class NotFoundException(message: String) : RuntimeException(message)

// Exception saat validasi input gagal (misal: stok habis, qty minus)
class ValidationException(val errors: Map<String, String>) : RuntimeException("Validation error")

// Exception saat ada konflik data (misal: email sudah terpakai)
class ConflictException(message: String) : RuntimeException(message)