package com.example.application.domain.model

data class Platillo(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    // Propiedades para los filtros
    val esRecomendacionChef: Boolean = false, // Filtro obligatorio
    val esFiltro1: Boolean = false, // Filtro variable 1
    val esFiltro2: Boolean = false,  // Filtro variable 2
    val img_url: String = ""
)