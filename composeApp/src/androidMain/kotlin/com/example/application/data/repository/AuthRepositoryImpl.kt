package com.example.application.data.repository


import com.example.application.domain.model.User
import com.example.application.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {
    override suspend fun registerUser(user: User): Boolean {
        // await como si fuera a la bd
        delay(1000) // 1 seg
        return true // return success
    }
}