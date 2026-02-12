package com.example.application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.domain.repository.SuperheroRepository

class SuperheroViewModelFactory(private val repository: SuperheroRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuperheroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SuperheroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}