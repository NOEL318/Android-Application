package com.example.application.domain.repository

import com.example.application.domain.model.Superhero

interface SuperheroRepository {
    suspend fun searchSuperheroes(query: String): Result<List<Superhero>>
}