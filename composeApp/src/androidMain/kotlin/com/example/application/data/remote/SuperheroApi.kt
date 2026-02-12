package com.example.application.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface SuperheroApi {
    // Reemplaza ACCESS_TOKEN con tu token real
    @GET("api/c2bb5215e151318de7c61fe96cd61f37/search/{name}")
    suspend fun searchByName(@Path("name") name: String): SuperheroSearchResponse
}


data class SuperheroSearchResponse(
    val response: String,
    @SerializedName("results-for") val resultsFor: String?,
    val results: List<SuperheroDTO>?
)

data class SuperheroDTO(
    val id: String,
    val name: String,
    val powerstats: PowerstatsDTO,
    val biography: BiographyDTO,
    val appearance: AppearanceDTO,
    val image: ImageDTO
)

data class PowerstatsDTO(
    val intelligence: String?,
    val strength: String?,
    val speed: String?
)

data class BiographyDTO(
    @SerializedName("full-name") val fullName: String?,
    val publisher: String?,
    val aliases: List<String>? // Ahora sí como lista para evitar el error anterior
)

data class AppearanceDTO(
    val gender: String?,
    val race: String?
)

data class ImageDTO(
    val url: String?
)
