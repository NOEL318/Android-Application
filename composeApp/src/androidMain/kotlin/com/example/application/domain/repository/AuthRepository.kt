package com.example.application.domain.repository

import com.example.application.domain.model.User


// Interface para invertir el uso del domain model
interface AuthRepository {
    suspend fun registerUser(user: User): Boolean
}