package com.example.application.domain.model

data class Superhero(
    val id: String,
    val name: String,
    val imageUrl: String,
    val gender: String,
    val race: String,
    val publisher: String,
    // Elegimos 3 poderes: Intelligence, Strength, Speed
    val intelligence: String,
    val strength: String,
    val speed: String
)