package com.example.application.data.repository

import com.example.application.data.remote.SuperheroApi
import com.example.application.data.remote.SuperheroDTO // Asegura este import
import com.example.application.domain.model.Superhero
import com.example.application.domain.repository.SuperheroRepository

class SuperheroRepositoryImpl(private val api: SuperheroApi) : SuperheroRepository {

    override suspend fun searchSuperheroes(query: String): Result<List<Superhero>> {
        return try {
            val response = api.searchByName(query)

            if (response.response == "success" && response.results != null) {
                val domainList = response.results.map { dto ->
                    Superhero(
                        id = dto.id,
                        name = dto.name,
                        imageUrl = dto.image.url ?: "",
                        gender = dto.appearance.gender ?: "Desconocido",
                        race = dto.appearance.race ?: "No definida",
                        publisher = dto.biography.publisher ?: "Independiente",
                        // Poderes seleccionados
                        intelligence = dto.powerstats.intelligence ?: "0",
                        strength = dto.powerstats.strength ?: "0",
                        speed = dto.powerstats.speed ?: "0"
                    )
                }
                Result.success(domainList)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}