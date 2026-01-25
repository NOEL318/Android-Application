package com.example.application.domain.usecase

import com.example.application.domain.model.User
import com.example.application.domain.repository.AuthRepository

class RegisterUserUseCase(private val repository: AuthRepository) {
    // validation y despues llamar a repository
    suspend operator fun invoke(nombre: String, email: String, pais: String): Result<Boolean> {
        if (nombre.isBlank() || email.isBlank()) {
            return Result.failure(Exception("Campos vacíos"))
        }
        val user = User(nombre, email, pais)
        val success = repository.registerUser(user)
        return if (success) Result.success(true) else Result.failure(Exception("Error al guardar"))
    }
}