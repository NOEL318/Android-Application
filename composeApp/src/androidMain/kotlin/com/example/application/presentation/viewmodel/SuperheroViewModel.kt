package com.example.application.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.domain.model.Superhero
import com.example.application.domain.repository.SuperheroRepository
import kotlinx.coroutines.launch

class SuperheroViewModel(private val repository: SuperheroRepository) : ViewModel() {
    var searchQuery by mutableStateOf("")
    var superheroes by mutableStateOf<List<Superhero>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun onSearch() {
        if (searchQuery.isBlank()) return

        viewModelScope.launch {
            isLoading = true

            val result = repository.searchSuperheroes(searchQuery)

            result.onSuccess { list ->
                superheroes = list // Actualiza la UI
            }.onFailure {
                // Loguea el error para saber qué falló
                Log.e("API_ERROR", "Error: ${it.message}")
            }
            isLoading = false
        }
    }
}